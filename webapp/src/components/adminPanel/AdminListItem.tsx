import React, { useState } from 'react'
import DeleteIcon from '@mui/icons-material/Delete'
import { TableCell, TableRow } from '@mui/material'
import { AdminStatusIndicator } from './AdminStatusIndicator'
import { api } from 'api'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { useNotification } from 'components/Context/NotificationContext'

interface Props {
  user: AdminUser
}

export const AdminListItem: React.FC<Props> = ({ user }) => {
  const notification = useNotification()
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [confirmMessage, setConfirmMessage] = useState('')
  const isDeleteConfirmed = () => {
    setShowSuccessModal(true)
    setConfirmMessage(
      'Are you sure that you want to remove ' + user.email + ' from admins?'
    )
  }
  const OnClickRemoveAdmin = async () => {
    const { status } = await api.admins.removeAdmin(user)
    if (status === 200) {
      notification(
        'Admin ' + user.fullName + ' has been removed successfully.',
        'success'
      )
    } else if (status === 409) {
      notification(
        'Admin ' +
          user.fullName +
          ' cannot been removed because he is assigned to wallet.',
        'error'
      )
    }
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
        confirmButtonContent="Yes"
        message={confirmMessage}
        onConfirm={() => OnClickRemoveAdmin()}
        open={showSuccessModal}
        setOpen={setShowSuccessModal}
      />
    </TableRow>
  )
}
