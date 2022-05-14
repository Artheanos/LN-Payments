import { Button } from '@mui/material'
import React from 'react'
import { useTranslation } from 'react-i18next'

import { useNotification } from 'components/Context/NotificationContext'

interface Props {
  id: number
  setDisplayToken: (token: string) => void
  token: string
}

export const TokenItem: React.FC<Props> = ({ id, token, setDisplayToken }) => {
  const notification = useNotification()
  const { t } = useTranslation('quickBuy')

  const tokenId = id + 1

  const copyToClipboard = () => {
    navigator.clipboard.writeText(token)
    notification(t('tokens.onCopyMessage', { tokenId }), 'success')
  }

  return (
    <div className="flex gap-16 justify-between items-center w-full">
      <div className="whitespace-nowrap">
        {t('tokens.listItemPrefix', { tokenId })}
      </div>
      <div className="flex gap-2">
        <Button onClick={() => copyToClipboard()} variant="text">
          {t('copy')}
        </Button>
        <Button
          variant="text"
          color="warning"
          onClick={() => setDisplayToken(token)}
        >
          {t('view')}
        </Button>
      </div>
    </div>
  )
}
