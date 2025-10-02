import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      component: () => import('../pages/LoginPage.vue'),
      meta: { public: true }
    },
    {
      path: '/signup',
      component: () => import('../pages/SignupPage.vue'),
      meta: { public: true }
    },
    {
      path: '/dashboard',
      component: () => import('../pages/DashboardPage.vue')
    },
    {
      path: '/groups',
      component: () => import('../pages/GroupsPage.vue')
    },
    {
      path: '/groups/:id',
      component: () => import('../pages/GroupDetailPage.vue'),
      props: true
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
});

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore();
  if (!to.meta.public && !auth.accessToken) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    return;
  }
  if (to.path === '/login' && auth.accessToken) {
    next('/dashboard');
    return;
  }
  next();
});

export default router;
