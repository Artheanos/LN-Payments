export const initialValues = (hostUrl: string | null) => ({
  email: '',
  password: '',
  hostUrl: hostUrl || 'http://',
})
