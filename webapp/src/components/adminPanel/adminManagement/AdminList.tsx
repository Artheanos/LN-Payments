import React from 'react'
import { TableCell, TableRow } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { api } from 'api'
import { PageableTable } from 'components/common/PageableTable/PageableTable'

export const AdminList: React.FC = () => {
  const { t } = useTranslation('common')

  return (
    <PageableTable
      apiRequest={api.admins.getAdmins}
      mapper={(user) => (
        <TableRow>
          <TableCell>{user.email}</TableCell>
          <TableCell>{user.fullName}</TableCell>
        </TableRow>
      )}
      headers={[t('email'), t('name')]}
    />
  )
}
