/**
 * Object with initial values for the login form.
 *
 * @param hostUrl  Backend URL inserted by the user. Passed as a parameter, because inserted once, it will be
 * always visible, so the user doesn't have to enter it each time he logs in.
 */
export const initialValues = (hostUrl: string | null) => ({
  email: '',
  password: '',
  hostUrl: hostUrl || 'http://',
})
