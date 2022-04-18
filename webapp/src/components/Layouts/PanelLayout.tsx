import React from 'react'
import { Box, Container } from '@mui/material'
import { Outlet } from 'react-router-dom'

import { Sidebar } from '../common/Sidebar'

export const PanelLayout = () => (
  <Box className="flex p-8 text-center">
    <Sidebar />
    <Container>
      <Outlet />
    </Container>
  </Box>
)
