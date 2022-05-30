import { Typography } from '@mui/material'
import React from 'react'

export type Color = {
  primary: string
  secondary: string
}

export const SuccessColor: Color = {
  primary: '#81c784',
  secondary: '#66bb6a'
}

export const FailColor: Color = {
  primary: '#e57373',
  secondary: '#f44336'
}

export const PendingColor: Color = {
  primary: '#4fc3f7',
  secondary: '#29b6f6'
}

type Props = {
  colorMapper: Record<string, Color>
  status: string
}

export const StatusCell: React.FC<Props> = (props: Props) => {
  const calculateColor = (
    status: string,
    colorMapper: Record<string, Color>
  ) => {
    const colors = colorMapper[status]
    return {
      backgroundColor: colors.primary,
      borderColor: colors.secondary
    }
  }

  return (
    <Typography
      align="center"
      sx={{
        borderRadius: 8,
        fontWeight: 'bold',
        display: 'inline-block',
        fontSize: '0.75rem',
        padding: '3px 10px',
        borderStyle: 'solid',
        borderWidth: '3px',
        width: '100%',
        ...calculateColor(props.status, props.colorMapper)
      }}
    >
      {props.status}
    </Typography>
  )
}
