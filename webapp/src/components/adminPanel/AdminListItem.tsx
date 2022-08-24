import React, { useContext, useState } from 'react'
import DeleteIcon from '@mui/icons-material/Delete'
import { TableCell, TableRow } from '@mui/material'
import { useTranslation } from 'react-i18next'

import { AdminStatusIndicator } from './AdminStatusIndicator'
import { AdminUser } from 'webService/interface/user'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { api } from 'webService/requests'
import { useNotification } from 'components/Context/NotificationContext'
import { UserContext } from 'components/Context/UserContext'

interface Props {
  adminUser: AdminUser
  reloadList: () => void
}

export const AdminListItem: React.FC<Props> = ({ adminUser, reloadList }) => {
  const { t } = useTranslation('auth')
  const { user } = useContext(UserContext)
  const notification = useNotification()
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [confirmMessage, setConfirmMessage] = useState('')

  const isDeleteConfirmed = () => {
    setShowSuccessModal(true)
    setConfirmMessage(t('remove.confirmMessage') + adminUser.email)
  }

  const onClickRemoveAdmin = async () => {
    const { status } = await api.admins.remove(adminUser)
    if (status === 200) {
      notification(adminUser.email + t('remove.successful'), 'success')
    } else if (status === 409) {
      notification(adminUser.fullName + t('remove.error'), 'error')
    } else {
      notification(t('auth:register.api.errors.default'), 'error')
    }
    reloadList()
  }

  return (
    <TableRow>
      <TableCell>{adminUser.email}</TableCell>
      <TableCell>{adminUser.fullName}</TableCell>
      <AdminStatusIndicator isValid={adminUser.hasKey} />
      <AdminStatusIndicator isValid={adminUser.isAssignedToWallet} />
      <TableCell>
        {!adminUser.isAssignedToWallet && user?.email !== adminUser.email && (
          <DeleteIcon
            onClick={isDeleteConfirmed}
            className="rounded-md border-2 border-red-500 cursor-pointer"
          />
        )}
      </TableCell>
      <ConfirmationModal
        confirmButtonContent={t('remove.form.confirmDeleteButton')}
        message={confirmMessage}
        onConfirm={() => onClickRemoveAdmin()}
        open={showSuccessModal}
        setOpen={setShowSuccessModal}
      />
    </TableRow>
  )
}
