import R from 'res/R'
import { NotificationDetails } from 'webService/interface/notification'

/**
 * Type defining screen names of sign in router. It enforces strict typing for navigators.
 */
export type SignInRouterProps = {
  [R.routes.drawer]: undefined
  [R.routes.notificationDetails]: NotificationDetails
  [R.routes.outcome]: {
    isConfirmation: boolean
  }
}
