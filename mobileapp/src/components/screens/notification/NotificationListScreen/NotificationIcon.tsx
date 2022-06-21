import React from 'react'
import { NotificationStatus } from 'webService/interface/notification'
import Icon from 'react-native-vector-icons/MaterialIcons'
import R from 'res/R'

type Props = {
  status: NotificationStatus
}

const size = 75

/**
 * Based on notification status returns a proper icon with associated color.
 *
 * @param status  Notification status {@see NotificationStatus}
 */
export const NotificationIcon: React.FC<Props> = ({ status }) => {
  switch (status) {
    case NotificationStatus.CONFIRMED:
      return (
        <Icon
          name="check-circle-outline"
          color={R.colors.green}
          size={size}
        ></Icon>
      )
    case NotificationStatus.PENDING:
      return (
        <Icon name="help-outline" color={R.colors.yellow} size={size}></Icon>
      )
    default:
      return <Icon name="highlight-off" color={R.colors.red} size={size}></Icon>
  }
}
