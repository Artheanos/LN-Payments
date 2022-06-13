import React from 'react'
import { NotificationStatus } from 'webService/interface/notification'
import Icon from 'react-native-vector-icons/MaterialIcons'

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
        <Icon name="check-circle-outline" color={'#66bb6a'} size={size}></Icon>
      )
    case NotificationStatus.PENDING:
      return <Icon name="help-outline" color={'#ffc107'} size={size}></Icon>
    default:
      return <Icon name="highlight-off" color={'#f44336'} size={size}></Icon>
  }
}
