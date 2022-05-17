import React, { useState } from 'react'
import { Button } from '@mui/material'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import Panel from 'components/common/Panel'
import routesBuilder from 'routesBuilder'
import { AdminListItem } from 'components/adminPanel/AdminListItem'
import { PageableTable } from 'components/common/PageableTable/PageableTable'
import { api } from 'api'
import { AdminUser } from 'common-ts/webServiceApi/interface/user'

export const AdminManagementPage: React.FC = () => {
  const { t } = useTranslation('adminManagement')
  const [reloadDependency, setReloadDependency] = useState(0)

  return (
    <Panel.Container>
      <Panel.Header title="Admin Management">
        <Link to={routesBuilder.userPanel.admins.create}>
          <Button variant="contained">Add new</Button>
        </Link>
      </Panel.Header>
      <Panel.Body table>
        <PageableTable
          apiRequest={api.admins.getAll}
          mapper={(user: AdminUser, key) => (
            <AdminListItem
              user={user}
              key={key}
              reloadList={() => setReloadDependency((i) => i + 1)}
            />
          )}
          headers={[
            t('common:email'),
            t('common:name'),
            t('hasKey'),
            t('isAssignedToWallet'),
            ''
          ]}
          reloadDependency={reloadDependency}
        />
      </Panel.Body>
    </Panel.Container>
  )
}
