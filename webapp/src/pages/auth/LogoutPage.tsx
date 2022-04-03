import React, { useContext, useEffect } from 'react'
import { UserContext } from 'components/Context/UserContext'
import { LinearProgress } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import routesBuilder from 'routesBuilder'

export const LogoutPage: React.FC = () => {
  const { logout, isLoggedIn } = useContext(UserContext)
  const navigate = useNavigate()

  useEffect(() => {
    if (isLoggedIn) {
      logout()
    }
    navigate(routesBuilder.landingPage)
  }, [logout, isLoggedIn, navigate])

  return <LinearProgress />
}
