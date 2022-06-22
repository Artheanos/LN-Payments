import { Buffer } from 'buffer'

/**
 * Converts Buffer to hex string.
 *
 * @param byteArray  Buffer that contains bytes.
 * @returns  Given value as hex string.
 */
export const toHexString = (byteArray: Buffer): string => {
  return Array.from(byteArray, function (byte) {
    return ('0' + (byte & 0xff).toString(16)).slice(-2)
  }).join('')
}

/**
 * Converts hex string to byte array.
 *
 * @param hexString  Hex string.
 * @returns  Byte array as Buffer object.
 */
export const toHexBytes = (hexString: string): Buffer => {
  return Buffer.from(hexString, 'hex')
}
