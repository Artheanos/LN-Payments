import QRCode from 'qrcode.react'
import React from 'react'
import { Grid } from '@mui/material'

import { useNotification } from 'components/Context/NotificationContext'

interface Props {
  value: string
  onCopyMessage: string
}

export const QRComponent: React.FC<Props> = ({
  value,
  onCopyMessage,
  children
}) => {
  const notification = useNotification()

  return (
    <>
      <Grid xs={12} item>
        {children}
      </Grid>
      <Grid xs={12} className="flex justify-center" item>
        <QRCode
          value={value}
          className="!w-full !h-auto cursor-pointer"
          onClick={() => {
            navigator.clipboard.writeText(value)
            notification(onCopyMessage, 'success')
          }}
          size={300}
        />
      </Grid>
    </>
  )
}
