/**
 * Object containing all strings used throughout the application.
 */
const strings = {
  common: {
    ok: 'OK',
  },
  notifications: {
    header: 'Notifications',
    emptyList: 'Nothing there yet',
  },
  login: {
    header: 'Login',
    submit: 'Login',
    invalidCredentials: 'Invalid credentials',
    unauthorized: 'This app is for admins only',
    invalidUrl: 'Invalid url',
  },
  logout: {
    action: 'Logging out...',
  },
  keyUpload: {
    alreadyUploaded: 'Key already uploaded',
    error: 'Error while uploading keys',
    redirecting: 'Redirecting',
    saving: 'Saving keys',
    uploading: 'Uploading keys',
    generating: 'Generating keys',
  },
  details: {
    loading: {
      text: 'Processing request...',
    },
    error: {
      header: 'Error occurred!',
      text: 'An error occurred when processing your request, please try again later.',
    },
    result: {
      confirmationText: 'Transaction confirmed successfully!',
      denialText: 'Transaction has been denied!',
    },
  },
}

export default strings
