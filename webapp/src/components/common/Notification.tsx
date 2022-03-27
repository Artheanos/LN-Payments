import React, { useContext } from 'react'
import { Alert, AlertColor, Snackbar } from '@mui/material'

import { NotificationContext } from '../Context/NotificationContext'

export const Notification: React.FC<{
  message: string
  severity: AlertColor
  autoHideDuration: number
  onClose: () => void
}> = ({ message, severity, autoHideDuration = 6000, onClose }) => {
  const { open } = useContext(NotificationContext)

  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={(_, reason) => {
        reason !== 'clickaway' && onClose()
      }}
    >
      <Alert severity={severity} onClose={onClose}>
        {message}
      </Alert>
    </Snackbar>
  )
}
