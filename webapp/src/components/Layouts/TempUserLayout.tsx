import React, { useContext } from 'react'
import { LinearProgress } from '@mui/material'
import { Navigate, Outlet } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'

export const TempUserLayout: React.FC = () => {
  const { isLoggedIn, loading } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isLoggedIn) return <Navigate to={routesBuilder.login} />

  return <Outlet />
}
