import React from 'react'
import { Outlet } from 'react-router-dom'

import { Navbar } from 'components/common/Navbar'
import { Box } from '@mui/material'

export const NavbarLayout: React.FC = () => {
  return (
    <Box className="flex">
      <Navbar />
      <Outlet />
    </Box>
  )
}
