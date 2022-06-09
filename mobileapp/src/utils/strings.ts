export const capitalize = (string: string) =>
  `${string.slice(0, 1).toUpperCase()}${string.slice(1)}`

export const isValidUrl = (url: string) => {
  try {
    new URL('', url)
    return true
  } catch (e) {
    return false
  }
}
