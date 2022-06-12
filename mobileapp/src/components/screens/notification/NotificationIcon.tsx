import React from 'react'
import { NotificationStatus } from 'webService/interface/notification'
import Icon from 'react-native-vector-icons/MaterialIcons'

type Props = {
  status: NotificationStatus
}

const size = 75

export const NotificationIcon: React.FC<Props> = ({ status }) => {
  switch (status) {
    case NotificationStatus.CONFIRMED:
      return (
        <Icon name="check-circle-outline" color={'FF0000'} size={size}></Icon>
      )
    case NotificationStatus.PENDING:
      return <Icon name="check-circle-outline" size={size}></Icon>
    default:
      return <Icon name="check-circle-outline" size={size}></Icon>
  }
}
