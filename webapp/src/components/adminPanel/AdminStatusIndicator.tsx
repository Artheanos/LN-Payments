import React from 'react'
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded'
import CancelRoundedIcon from '@mui/icons-material/CancelRounded'
import { TableCell } from '@mui/material'
import { green, red } from '@mui/material/colors'

interface Props {
  isValid: boolean
}

export const AdminStatusIndicator: React.FC<Props> = ({ isValid }) => {
  return (
    <TableCell>
      {isValid ? (
        <CheckCircleRoundedIcon sx={{ color: green[700] }} />
      ) : (
        <CancelRoundedIcon sx={{ color: red[700] }} />
      )}
    </TableCell>
  )
}
