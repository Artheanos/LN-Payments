import React, { useContext } from 'react'
import { LinearProgress } from '@mui/material'
import { Navigate } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'
import { Role } from '@constants'
import { PanelLayout } from './PanelLayout'

export const AdminLayout: React.FC = () => {
  const { user, isLoggedIn, loading } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isLoggedIn) return <Navigate to={routesBuilder.login} />

  if (user?.role !== Role.ADMIN)
    return <Navigate to={routesBuilder.landingPage} />

  return <PanelLayout />
}
