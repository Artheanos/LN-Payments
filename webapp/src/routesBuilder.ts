export default {
  landingPage: '/',
  quickBuy: '/quick-buy',
  register: '/register',
  login: '/login',
  history: '/history',
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
    }
  }
}
