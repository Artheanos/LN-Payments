import React, { useCallback, useState } from 'react'
import { Alert, Button, Card } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { StageProps } from 'components/QuickBuy/StageProps'
import { TokenList } from './TokenList'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'

export const TokensStage: React.FC<StageProps> = ({
  onNext,
  tokens,
  setTokens
}) => {
  const { t } = useTranslation('quickBuy')
  const [displayToken, setDisplayToken] = useState<string>()

  const closeTransaction = useCallback(() => {
    setTokens(undefined)
    onNext()
  }, [onNext, setTokens])

  if (!tokens) return null

  return (
    <div className="flex flex-col gap-10">
      <Alert variant="standard" severity="info" className="mx-auto">
        {t('tokens.info')}
      </Alert>
      <div className="mx-auto w-min">
        <Card>
          <TokenList tokens={tokens} setDisplayToken={setDisplayToken} />
        </Card>
        <div className="flex justify-end mt-5">
          <Button
            variant="contained"
            onClick={() => closeTransaction()}
            color="warning"
          >
            {t('close')}
          </Button>
        </div>
        <ConfirmationModal
          open={Boolean(displayToken)}
          message={displayToken || ''}
          setOpen={() => setDisplayToken(undefined)}
          confirmButtonContent={t('close')}
        />
      </div>
    </div>
  )
}
