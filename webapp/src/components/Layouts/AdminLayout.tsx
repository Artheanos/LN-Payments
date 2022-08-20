import React, { useContext } from 'react'
import { Navigate } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'
import { PanelLayout } from './PanelLayout'
import { Role } from 'webService/interface/user'

export const AdminLayout: React.FC = () => {
  const { user, isLoggedIn } = useContext(UserContext)

  if (!isLoggedIn) return <Navigate to={routesBuilder.login} />

  if (user?.role !== Role.ADMIN)
    return <Navigate to={routesBuilder.landingPage} />

  return <PanelLayout />
}
