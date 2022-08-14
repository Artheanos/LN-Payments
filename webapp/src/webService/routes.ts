export const routes = {
  payments: {
    all: '/api/payments/all',
    ws: (host?: string) => `ws://${host || window.location.host}/api/payment`,
    index: '/api/payments',
    info: '/api/payments/info'
  },
  auth: {
    login: '/api/auth/login',
    refreshToken: '/api/auth/refreshToken',
    register: '/api/auth/register',
    temporary: '/api/auth/temporary'
  },
  admins: {
    index: '/api/admins',
    keys: '/api/admins/keys'
  },
  wallet: {
    index: '/api/wallet',
    closeChannels: '/api/wallet/closeChannels',
    transfer: '/api/wallet/transfer'
  },
  transactions: {
    index: '/api/transactions',
    newInfo: '/api/transactions/info'
  },
  settings: {
    index: '/api/settings'
  },
  users: {
    index: '/api/users',
    password: '/api/users/password'
  }
}
