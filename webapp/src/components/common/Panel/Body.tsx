import React from 'react'
import { Box } from '@mui/material'

interface Props {
  table?: boolean
}

export const Body: React.FC<Props> = ({ children, table }) => {
  const className = table ? 'px-7' : 'px-10 pb-10'

  return <Box className={className}>{children}</Box>
}
