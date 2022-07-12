import React, { useCallback, useContext, useState } from 'react'
import { Alert, Button, Card } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { StageProps } from 'components/QuickBuy/StageProps'
import { TokenList } from './TokenList'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { UserContext } from 'components/Context/UserContext'

export const TokensStage: React.FC<StageProps> = ({
  onNext,
  tokens,
  setTokens
}) => {
  const { t } = useTranslation('quickBuy')
  const [displayToken, setDisplayToken] = useState<string>()
  const { hasAccount } = useContext(UserContext)

  const closeTransaction = useCallback(() => {
    setTokens(undefined)
    onNext()
  }, [onNext, setTokens])

  if (!tokens) return null

  return (
    <div className="flex flex-col gap-10">
      {!hasAccount && (
        <Alert variant="standard" severity="warning" className="mx-auto">
          {t('tokens.infoWithoutAccount')}
        </Alert>
      )}
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
            {t('common:close')}
          </Button>
        </div>
        <ConfirmationModal
          open={Boolean(displayToken)}
          message={displayToken || ''}
          setOpen={() => setDisplayToken(undefined)}
          confirmButtonContent={t('common:close')}
        />
      </div>
    </div>
  )
}
