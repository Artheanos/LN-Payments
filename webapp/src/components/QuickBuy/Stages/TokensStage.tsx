import React, { useCallback } from 'react'
import { Button, Card, List, ListItem } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { LocalKey } from '@constants'
import { StageProps } from '../StageProps'
import { useLocalStorage } from 'utils/persist'

export const TokensStage: React.FC<StageProps> = ({ onNext }) => {
  const { t } = useTranslation('common')
  const [tokens, setTokens] = useLocalStorage<string[]>(
    LocalKey.TRANSACTION_TOKENS
  )

  const closeTransaction = useCallback(() => {
    setTokens(undefined)
    onNext()
  }, [onNext, setTokens])

  return (
    <div className="mx-auto w-64">
      <Card>
        <List>
          {tokens?.map((token) => (
            <ListItem key={token}>{token}</ListItem>
          ))}
        </List>
      </Card>
      <div className="flex justify-end mt-5">
        <Button variant="contained" onClick={() => closeTransaction()}>
          {t('close')}
        </Button>
      </div>
    </div>
  )
}
