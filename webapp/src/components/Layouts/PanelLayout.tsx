import React from 'react'
import { Sidebar } from '../common/Sidebar'
import { Box, Container } from '@mui/material'
import { Outlet } from 'react-router-dom'

export const PanelLayout = () => (
  <Box className="flex px-8 pt-8">
    <Sidebar />
    <Container>
      <Outlet />
    </Container>
  </Box>
)
