/**
 * Capitalizes the string.
 *
 * @param string  String to capitalize.
 */
export const capitalize = (string: string) =>
  `${string.slice(0, 1).toUpperCase()}${string.slice(1)}`

/**
 * Verifies if the given string is a valid URL.
 *
 * @param url  String containing URL to be checked.
 */
export const isValidUrl = (url: string) => {
  try {
    new URL('', url)
    return true
  } catch (e) {
    return false
  }
}
