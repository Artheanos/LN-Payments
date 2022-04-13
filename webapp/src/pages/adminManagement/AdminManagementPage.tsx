import React from 'react'
import { Button } from '@mui/material'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import Panel from 'components/common/Panel'
import routesBuilder from 'routesBuilder'
import { AdminListItem } from 'components/adminPanel/adminManagement/AdminListItem'
import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { api } from 'api'

export const AdminManagementPage: React.FC = () => {
  const { t } = useTranslation('common')

  return (
    <Panel.Container>
      <Panel.Header title="Admin Management">
        <Link to={routesBuilder.adminPanel.admins.create}>
          <Button variant="contained">Add new</Button>
        </Link>
      </Panel.Header>
      <Panel.Body>
        <PageableTable
          apiRequest={api.admins.getAdmins}
          mapper={(user, key) => <AdminListItem user={user} key={key} />}
          headers={[t('email'), t('name'), 'Is assigned to a wallet', '']}
        />
      </Panel.Body>
    </Panel.Container>
  )
}