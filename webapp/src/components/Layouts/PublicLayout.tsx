import React, { useContext } from 'react'

import routesBuilder from 'routesBuilder'
import { UserContext } from '../Context/UserContext'
import { Navigate, Outlet } from 'react-router-dom'
import { Role } from 'webService/interface/user'

export const PublicLayout: React.FC = () => {
  const { user, isLoggedIn } = useContext(UserContext)

  if (!isLoggedIn || user?.role === Role.TEMPORARY) return <Outlet />
  if (user?.role === Role.ADMIN) {
    return <Navigate to={routesBuilder.userPanel.index} />
  }
  return <Navigate to={routesBuilder.userPanel.index} />
}
