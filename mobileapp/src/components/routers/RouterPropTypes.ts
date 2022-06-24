import R from 'res/R'

/**
 * Type defining screen names of sign in router. It enforces strict typing for navigators.
 */
export type SignInRouterProps = {
  [R.routes.drawer]: undefined
  [R.routes.notificationDetails]: { notificationId: string }
  [R.routes.outcome]: {
    isConfirmation: boolean
  }
}
