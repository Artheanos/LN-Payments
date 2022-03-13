import React, { useContext } from 'react'
import {Box, LinearProgress} from '@mui/material'
import { Navigate, Outlet } from 'react-router-dom'

import routesBuilder from 'routesBuilder'
import { UserContext } from 'components/Context/UserContext'
import { Sidebar } from '../common/Sidebar'

export const UserLayout: React.FC = () => {
  const { isValid, loading } = useContext(UserContext)

  if (loading) return <LinearProgress />
  if (!isValid) return <Navigate to={routesBuilder.login} />
  return (
    <>
      <Sidebar />
      <Outlet />
    </>
  )
}
