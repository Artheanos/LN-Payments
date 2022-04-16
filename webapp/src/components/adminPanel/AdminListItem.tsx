import DeleteIcon from '@mui/icons-material/Delete'
import React from 'react'
import { Link } from 'react-router-dom'
import { TableCell, TableRow } from '@mui/material'
import { AdminStatusIndicator } from './AdminStatusIndicator'

interface Props {
  user: AdminUser
}

export const AdminListItem: React.FC<Props> = ({ user }) => {
  return (
    <TableRow>
      <TableCell>{user.email}</TableCell>
      <TableCell>{user.fullName}</TableCell>
      <AdminStatusIndicator isValid={user.hasKey} />
      <AdminStatusIndicator isValid={user.isAssignedToWallet} />
      <TableCell>
        <Link to="delete">
          <DeleteIcon className="rounded-md border-2 border-red-500 cursor-pointer" />
        </Link>
      </TableCell>
    </TableRow>
  )
}
