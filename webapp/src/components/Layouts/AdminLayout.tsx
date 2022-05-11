import React, { useContext } from 'react'
import { LinearProgress } from '@mui/material'
import { Navigate } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'
import { PanelLayout } from './PanelLayout'
import { Role } from 'common-ts/dist/webServiceApi/interface/user'

export const AdminLayout: React.FC = () => {
  const { user, isLoggedIn, loading } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isLoggedIn) return <Navigate to={routesBuilder.login} />

  if (user?.role !== Role.ADMIN)
    return <Navigate to={routesBuilder.landingPage} />

  return <PanelLayout />
}
