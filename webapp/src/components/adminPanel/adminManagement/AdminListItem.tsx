import DeleteIcon from '@mui/icons-material/Delete'
import React from 'react'
import { Link } from 'react-router-dom'
import { TableCell, TableRow } from '@mui/material'

interface Props {
  user: User
}

export const AdminListItem: React.FC<Props> = ({ user }) => (
  <TableRow>
    <TableCell>{user.fullName}</TableCell>
    <TableCell>{user.email}</TableCell>
    <TableCell>TODO</TableCell>
    <TableCell>
      <Link to="delete">
        <DeleteIcon className="rounded-md border-2 border-red-500 cursor-pointer" />
      </Link>
    </TableCell>
  </TableRow>
)
