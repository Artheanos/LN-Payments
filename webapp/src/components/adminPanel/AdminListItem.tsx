import React, { useState } from 'react'
import DeleteIcon from '@mui/icons-material/Delete'
import { TableCell, TableRow } from '@mui/material'
import { AdminStatusIndicator } from './AdminStatusIndicator'
import { api } from 'api'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { useNotification } from 'components/Context/NotificationContext'
import { useTranslation } from 'react-i18next'

interface Props {
  user: AdminUser
  reloadList: () => void
}

export const AdminListItem: React.FC<Props> = ({ user, reloadList }) => {
  const { t } = useTranslation('auth')
  const notification = useNotification()
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [confirmMessage, setConfirmMessage] = useState('')

  const isDeleteConfirmed = () => {
    setShowSuccessModal(true)
    setConfirmMessage(t('remove.confirmMessage') + user.email)
  }

  const onClickRemoveAdmin = async () => {
    const { status } = await api.admins.remove(user)
    if (status === 200) {
      notification(user.email + t('remove.successful'), 'success')
    } else if (status === 409) {
      notification(user.fullName + t('remove.error'), 'error')
    } else {
      notification(t('register.api.errors.default'), 'error')
    }
    reloadList()
  }

  return (
    <TableRow>
      <TableCell>{user.email}</TableCell>
      <TableCell>{user.fullName}</TableCell>
      <AdminStatusIndicator isValid={user.hasKey} />
      <AdminStatusIndicator isValid={user.isAssignedToWallet} />
      <TableCell>
        {!user.isAssignedToWallet && (
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
