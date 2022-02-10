import React from 'react'
import { Button, Card, List, ListItem } from '@mui/material'

import { StageProps } from '../StageProps'
import { useTranslation } from 'react-i18next'

export const TokensStage: React.FC<StageProps> = ({ onNext }) => {
  const { t } = useTranslation('common')

  return (
    <div className="mx-auto w-64">
      <Card>
        <List>
          <ListItem>1. Token 1</ListItem>
          <ListItem>2. Token 2</ListItem>
        </List>
      </Card>
      <div className="flex justify-end mt-5">
        <Button variant="contained" onClick={onNext}>
          {t('close')}
        </Button>
      </div>
    </div>
  )
}
