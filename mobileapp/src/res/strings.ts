/**
 * Object containing all strings used throughout the application.
 */
const strings = {
  common: {
    ok: 'OK',
    currency: {
      satoshi: 'SAT',
    },
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
    error: 'Exception occurred when logging in, try again later',
  },
  logout: {
    action: 'Logging out...',
    connectionError: 'Could not connect to the server',
    timeout: "Timeout, you've been logged out",
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
    id: 'ID',
    type: 'Type',
    message: 'Message',
    address: 'Destination address',
    amount: 'Amount',
    status: 'Status',
    btnConfirm: 'Confirm',
    btnDeny: 'Deny',
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
