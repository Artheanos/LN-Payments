import React, { FormEvent } from 'react'
import { Button, TextField } from '@mui/material'

import { StageProps } from '../StageProps'
import { useTranslation } from 'react-i18next'

export const SetupStage: React.FC<StageProps> = ({ onNext }) => {
  const { t } = useTranslation('common')

  const onSubmit = (e: FormEvent) => {
    e.preventDefault()
    onNext()
  }

  return (
    <div>
      <div>{t('quickBuy.setup.info')}</div>
      <form
        onSubmit={onSubmit}
        className="flex flex-col gap-4 mx-auto mt-12 w-64"
      >
        <TextField
          id="tokenCount"
          label={t('quickBuy.setup.form.tokenCount.label')}
          variant="outlined"
          type="number"
          defaultValue="1"
        />
        <TextField
          id="email"
          label={t('quickBuy.setup.form.email.label')}
          variant="outlined"
        />
        <div className="flex justify-end">
          <Button variant="contained" className="bg-purple-300" type="submit">
            {t('next')}
          </Button>
        </div>
      </form>
    </div>
  )
}
