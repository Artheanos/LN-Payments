export const routes = {
  auth: {
    login: '/api/auth/login',
  },
  admins: {
    keys: '/api/admins/keys',
  },
  notifications: {
    index: '/api/notifications',
    transaction: (id: string) => `/api/notifications/${id}/transaction`,
    confirm: (id: string) => `/api/notifications/${id}/confirm`,
    deny: (id: string) => `/api/notifications/${id}/deny`,
  },
}
