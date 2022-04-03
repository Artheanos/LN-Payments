import React from 'react'
import { Outlet } from 'react-router-dom'

import { Navbar } from 'components/common/Navbar'
import { Box, Toolbar } from '@mui/material'

export const NavbarLayout: React.FC = () => {
  return (
    <Box>
      <Navbar />
      <div>
        <Toolbar />
        <Outlet />
      </div>
    </Box>
  )
}
