import { Box } from '@mui/material'
import React from 'react'

export const Header: React.FC<{ title: string }> = ({ title, children }) => (
  <Box className="flex justify-between p-10" sx={{ '& h1': {} }}>
    <span className="text-2xl font-bold">{title}</span>
    {children}
  </Box>
)
