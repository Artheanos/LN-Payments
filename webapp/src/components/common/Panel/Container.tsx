import React from 'react'
import { Paper } from '@mui/material'

interface Props {
  className?: string
}

export const Container: React.FC<Props> = ({ children, className }) => (
  <Paper className={`text-left ${className}`}>{children}</Paper>
)
