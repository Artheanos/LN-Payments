import React, { useContext } from 'react'
import { LinearProgress } from '@mui/material'
import { Navigate, Outlet } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'

export const UserLayout: React.FC = () => {
  const { isValid, loading } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isValid) return <Navigate to={routesBuilder.login} />
  return <Outlet />
}
