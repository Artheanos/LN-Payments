import React, { useContext } from 'react'
import { LinearProgress } from '@mui/material'
import { Navigate } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'
import { PanelLayout } from './PanelLayout'
import { Role } from 'common-ts/webServiceApi/interface/user'

export const UserLayout: React.FC = () => {
  const { isLoggedIn, loading, user } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isLoggedIn) return <Navigate to={routesBuilder.login} />

  if (user?.role === Role.TEMPORARY)
    return <Navigate to={routesBuilder.landingPage} />

  return <PanelLayout />
}
