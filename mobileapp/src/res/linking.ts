import routes from './routes'

const prefix = 'lnpayments://'
const addPrefix = (value: string) => `${prefix}${value}`
const removePrefix = (value: string) => value.substring(prefix.length)

/**
 * Routes for deep linking
 */
const screens = {
  // prettier-ignore
  [routes.notificationDetails]: (id?: string) => addPrefix(`notification/${id || ':notificationId'}`),
  // prettier-enable
}

/**
 * Routes with functions' default results
 * used for configuration
 */
const configScreens = () => {
  const result: Record<string, string> = {}
  Object.entries(screens).forEach(([routeName, value]) => {
    result[routeName] = removePrefix(value())
  })
  return result
}

const linking = {
  prefix,
  screens,
  configScreens: configScreens(),
}

export default linking
