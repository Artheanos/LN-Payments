import React, { useContext } from 'react'

import routesBuilder from 'routesBuilder'
import { UserContext } from '../Context/UserContext'
import { Navigate, Outlet } from 'react-router-dom'
import { Role } from '@constants'

export const PublicLayout: React.FC = () => {
  const { user, isLoggedIn } = useContext(UserContext)

  if (!isLoggedIn) return <Outlet />
  if (user?.role === Role.ADMIN) {
    return <Navigate to={routesBuilder.adminPanel.index} />
  }
  return <Navigate to={routesBuilder.userPanel.index} />
}
