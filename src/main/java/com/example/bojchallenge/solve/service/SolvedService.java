package com.example.bojchallenge.solve.service;

import com.example.bojchallenge.solve.dto.SolvedProfile;
import com.example.bojchallenge.solve.dto.TodaySolveSummary;
import com.example.bojchallenge.solve.entity.DailySolve;
import com.example.bojchallenge.solve.entity.SolveSnapshot;
import com.example.bojchallenge.solve.repository.DailySolveRepository;
import com.example.bojchallenge.solve.repository.SolveSnapshotRepository;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.repository.UserRepository;
import com.example.bojchallenge.util.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolvedService {

    private final RestClient solvedAcRestClient;
    private final ObjectMapper objectMapper;
    private final SolveSnapshotRepository solveSnapshotRepository;
    private final DailySolveRepository dailySolveRepository;
    private final UserRepository userRepository;

    private final Map<String, CachedProfile> profileCache = new ConcurrentHashMap<>();
    private final Map<String, CachedSolvedList> solvedCache = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        log.info("SolvedService initialized with in-memory cache");
    }

    public SolvedProfile getUserProfile(String handle) {
        CachedProfile cached = profileCache.get(handle);
        if (cached != null && !cached.isExpired()) {
            return cached.profile();
        }
        try {
            String body = solvedAcRestClient.get()
                .uri("/user/show?handle={handle}", handle)
                .retrieve()
                .body(String.class);
            JsonNode node = objectMapper.readTree(body);
            SolvedProfile profile = new SolvedProfile(
                handle,
                node.path("solvedCount").asInt(0),
                node.path("tier").asInt(0),
                node.path("rating").asInt(0)
            );
            profileCache.put(handle, new CachedProfile(profile, Instant.now()));
            return profile;
        } catch (RestClientException | JsonProcessingException e) {
            log.warn("Failed to fetch profile for {}: {}", handle, e.getMessage());
            if (cached != null) {
                return cached.profile();
            }
            return new SolvedProfile(handle, 0, 0, 0);
        }
    }

    public List<Integer> fetchRecentSolvedProblems(String handle, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        CachedSolvedList cached = solvedCache.get(handle);
        if (cached != null && !cached.isExpired() && cached.ids().size() >= limit) {
            return cached.ids().subList(0, limit);
        }
        List<Integer> result = new ArrayList<>();
        int page = 1;
        while (result.size() < limit && page <= 5) {
            try {
                String body = solvedAcRestClient.get()
                    .uri("/user/solved?handle={handle}&page={page}", handle, page)
                    .retrieve()
                    .body(String.class);
                List<Integer> ids = parseSolvedIds(body);
                if (ids.isEmpty()) {
                    break;
                }
                result.addAll(ids);
            } catch (RestClientException | JsonProcessingException e) {
                log.warn("Failed to fetch solved problem list for {}: {}", handle, e.getMessage());
                break;
            }
            page++;
        }
        if (result.isEmpty() && cached != null) {
            return cached.ids().subList(0, Math.min(limit, cached.ids().size()));
        }
        List<Integer> truncated = result.subList(0, Math.min(limit, result.size()));
        solvedCache.put(handle, new CachedSolvedList(new ArrayList<>(result), Instant.now()));
        return truncated;
    }

    private List<Integer> parseSolvedIds(String body) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(body);
        List<Integer> ids = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> ids.add(item.path("problemId").asInt()));
        } else {
            node.path("items").forEach(item -> ids.add(item.path("problemId").asInt()));
        }
        return ids.stream().filter(id -> id > 0).distinct().toList();
    }

    @Transactional
    public TodaySolveSummary computeTodaySolved(User user) {
        LocalDate today = DateUtils.today();
        SolvedProfile profile = getUserProfile(user.getBojHandle());
        SolveSnapshot snapshot = solveSnapshotRepository.findByUserIdAndDate(user.getId(), today)
            .orElseGet(() -> solveSnapshotRepository.save(SolveSnapshot.builder()
                .user(user)
                .date(today)
                .totalSolved(profile.totalSolved())
                .build()));

        int delta = Math.max(profile.totalSolved() - snapshot.getTotalSolved(), 0);
        List<Integer> problemIds = List.copyOf(fetchRecentSolvedProblems(user.getBojHandle(), delta));

        DailySolve dailySolve = dailySolveRepository.findByUserIdAndDate(user.getId(), today)
            .orElseGet(() -> dailySolveRepository.save(DailySolve.builder()
                .user(user)
                .date(today)
                .solvedCount(0)
                .problemNumbers("[]")
                .build()));
        String serialized = serialize(problemIds);
        if (delta != dailySolve.getSolvedCount() || !serialized.equals(dailySolve.getProblemNumbers())) {
            dailySolve.updateSolve(delta, serialized);
        }

        return new TodaySolveSummary(delta, problemIds);
    }

    private String serialize(List<Integer> ids) {
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Transactional
    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void takeMidnightSnapshotForAllUsers() {
        LocalDate today = DateUtils.today();
        userRepository.findAll().forEach(user -> {
            SolvedProfile profile = getUserProfile(user.getBojHandle());
            SolveSnapshot snapshot = solveSnapshotRepository.findByUserIdAndDate(user.getId(), today)
                .orElseGet(() -> SolveSnapshot.builder()
                    .user(user)
                    .date(today)
                    .totalSolved(profile.totalSolved())
                    .build());
            snapshot.updateTotalSolved(profile.totalSolved());
            solveSnapshotRepository.save(snapshot);

            dailySolveRepository.findByUserIdAndDate(user.getId(), today)
                .ifPresentOrElse(existing -> existing.updateSolve(0, "[]"),
                    () -> dailySolveRepository.save(DailySolve.builder()
                        .user(user)
                        .date(today)
                        .solvedCount(0)
                        .problemNumbers("[]")
                        .build()));
        });
        log.info("Midnight snapshot completed for {} users", userRepository.count());
    }

    public void refreshCache(String handle) {
        profileCache.remove(handle);
        solvedCache.remove(handle);
    }

    private record CachedProfile(SolvedProfile profile, Instant fetchedAt) {
        private boolean isExpired() {
            return fetchedAt.isBefore(Instant.now().minusSeconds(600));
        }
    }

    private record CachedSolvedList(List<Integer> ids, Instant fetchedAt) {
        private boolean isExpired() {
            return fetchedAt.isBefore(Instant.now().minusSeconds(300));
        }
    }
}
