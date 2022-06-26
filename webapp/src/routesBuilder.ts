export default {
  landingPage: '/',
  login: '/login',
  logout: '/logout',
  quickBuy: '/quick-buy',
  register: '/register',
  userPanel: {
    admins: {
      index: '/panel/admins',
      create: '/panel/admins/new'
    },
    history: '/panel/history',
    index: '/panel',
    payments: '/panel/payments',
    quickBuy: '/panel/quick-buy',
    wallet: {
      index: '/panel/wallet',
      new: '/panel/wallet/new'
    },
    transactions: {
      index: '/panel/transactions',
      new: '/panel/transactions/new'
    },
    serverSettings: {
      index: '/panel/settings'
    }
  },
  api: {
    payments: {
      all: '/api/payments/all',
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
    },
    wallet: {
      index: '/api/wallet',
      closeChannels: '/api/wallet/closeChannels',
      transfer: '/api/wallet/transfer'
    }
  }
}
