import React, { ReactElement, useCallback, useContext, useState } from 'react'
import { AlertColor } from '@mui/material'

import { Notification } from 'components/common/Notification'

type ContextType = {
  setElement: (element: ReactElement | null) => void
  element: ReactElement | null
  open: boolean
  setOpen: (open: boolean) => void
}

export const NotificationContext = React.createContext<ContextType>({
  setElement: (_) => {},
  element: null,
  open: false,
  setOpen: () => {}
})

export const NotificationProvider: React.FC = ({ children }) => {
  const [element, setElement] = useState<ReactElement | null>(null)
  const [open, setOpen] = useState(false)

  return (
    <NotificationContext.Provider
      value={{ element, setElement, open, setOpen }}
    >
      {children}
      <GlobalElement />
    </NotificationContext.Provider>
  )
}

const GlobalElement: React.FC = () => useContext(NotificationContext).element

export const useNotification = () => {
  const { open, setOpen, setElement } = useContext(NotificationContext)

  return useCallback(
    (message: string, severity: AlertColor, autoHideDuration = 4000) => {
      if (open) {
        setOpen(false)
        setTimeout(() => setOpen(true), 100)
      } else {
        setOpen(true)
      }

      setElement(
        <Notification
          message={message}
          severity={severity}
          autoHideDuration={autoHideDuration}
          onClose={() => setOpen(false)}
        />
      )
    },
    [open, setOpen, setElement]
  )
}
