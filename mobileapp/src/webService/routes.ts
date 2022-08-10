/**
 * Contains router for backend API calls.
 */
export const routes = {
  auth: {
    login: '/api/auth/login',
  },
  admins: {
    keys: '/api/admins/keys',
  },
  notifications: {
    index: '/api/notifications',
    details: (id: string) => `/api/notifications/${id}`,
    transaction: (id: string) => `/api/notifications/${id}/transaction`,
    confirm: (id: string) => `/api/notifications/${id}/confirm`,
    deny: (id: string) => `/api/notifications/${id}/deny`,
  },
  transactions: {
    index: '/api/transactions',
    ws: (hostUrl: string) =>
      new URL('/api/notification', hostUrl).href?.replace('http://', 'ws://'),
  },
}
