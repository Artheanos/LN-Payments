import R from 'res/R'
import { NotificationDetails } from 'webService/interface/notification'

export type SignInRouterProps = {
  [R.routes.drawer]: undefined
  [R.routes.notificationDetails]: NotificationDetails
}
