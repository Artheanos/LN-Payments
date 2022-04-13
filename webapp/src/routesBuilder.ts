export default {
  landingPage: '/',
  login: '/login',
  logout: '/logout',
  quickBuy: '/quick-buy',
  register: '/register',
  adminPanel: {
    admins: {
      index: '/admin-panel/admins',
      create: '/admin-panel/admins/new'
    },
    history: '/admin-panel/history',
    index: '/admin-panel',
    quickBuy: '/admin-panel/quick-buy'
  },
  userPanel: {
    history: '/panel/history',
    index: '/panel',
    quickBuy: '/panel/quick-buy'
  },
  api: {
    payments: {
      ws: () => `ws://${window.location.host}/api/payment`,
      index: '/api/payments',
      info: '/api/payments/info'
    },
    auth: {
      register: '/api/auth/register',
      login: '/api/auth/login',
      refreshToken: '/api/auth/refreshToken'
    },
    admins: {
      index: '/api/admins'
    }
  }
}
