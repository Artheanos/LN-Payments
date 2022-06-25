import '../../shim'

// eslint-disable-next-line @typescript-eslint/no-var-requires
const bitcoinjs = require('bitcoinjs-lib')
/**
 * Exports bitcoinjs library, so it can be used throughout the application.
 * This step is required because this dependency cannot be directly used in react native apps,
 * we are doing some hacks with shim file to enable it.
 */
export default bitcoinjs
