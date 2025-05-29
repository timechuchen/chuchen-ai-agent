import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      title: '首页 - 初晨AI超级智能体应用平台',
      description: '初晨AI超级智能体应用平台提供AI旅游大师和AI超级智能体服务，满足您的各种AI对话需求'
    }
  },
  {
    path: '/tourism-master',
    name: 'TourismMaster',
    component: () => import('../views/TourismMaster.vue'),
    meta: {
      title: 'AI导游大师 - 初晨AI超级智能体应用平台',
      description: 'AI旅游大师是初晨AI超级智能体应用平台的专业情感顾问，帮你解答各种旅游问题，提供情感建议'
    }
  },
  {
    path: '/super-agent',
    name: 'SuperAgent',
    component: () => import('../views/SuperAgent.vue'),
    meta: {
      title: 'AI超级智能体 - 初晨AI超级智能体应用平台',
      description: 'AI超级智能体是初晨AI超级智能体应用平台的全能助手，能解答各类专业问题，提供精准建议和解决方案'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫，设置文档标题
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router
