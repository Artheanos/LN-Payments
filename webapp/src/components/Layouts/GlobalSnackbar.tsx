import React, { ReactElement, useCallback, useContext, useState } from 'react'
import { Alert, AlertColor, Snackbar } from '@mui/material'

type SnackbarContextType = {
  setElement: (element: ReactElement | null) => void
  element: ReactElement | null
  open: boolean
  setOpen: (open: boolean) => void
}

export const SnackbarContext = React.createContext<SnackbarContextType>({
  setElement: (_) => {},
  element: null,
  open: false,
  setOpen: () => {}
})

export const SnackbarProvider: React.FC = ({ children }) => {
  const [element, setElement] = useState<ReactElement | null>(null)
  const [open, setOpen] = useState(false)

  return (
    <SnackbarContext.Provider value={{ element, setElement, open, setOpen }}>
      {children}
      <GlobalElement />
    </SnackbarContext.Provider>
  )
}

export const GlobalElement: React.FC = () => useContext(SnackbarContext).element

const CoolSnackbar: React.FC<{
  message: string
  severity: AlertColor
  autoHideDuration: number
  onClose: () => void
}> = ({ message, severity, autoHideDuration = 6000, onClose }) => {
  const { open } = useContext(SnackbarContext)

  return (
    <Snackbar open={open} autoHideDuration={autoHideDuration} onClose={onClose}>
      <Alert severity={severity} onClose={onClose}>
        {message}
      </Alert>
    </Snackbar>
  )
}

export const useGlobalSnackbar = () => {
  const { setOpen, setElement } = useContext(SnackbarContext)

  return useCallback(
    (message: string, severity: AlertColor, autoHideDuration = 6000) => {
      setOpen(true)
      setElement(
        <CoolSnackbar
          message={message}
          severity={severity}
          autoHideDuration={autoHideDuration}
          onClose={() => setOpen(false)}
        />
      )
    },
    [setElement, setOpen]
  )
}
