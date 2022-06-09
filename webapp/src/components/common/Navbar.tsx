import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings'
import React, { useContext } from 'react'
import { AppBar, Toolbar } from '@mui/material'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { UserContext } from '../Context/UserContext'
import { Role } from 'webService/interface/user'

export const Navbar: React.FC = () => {
  const { t } = useTranslation('common')
  const { user } = useContext(UserContext)

  return (
    <AppBar
      position="fixed"
      className="pl-5"
      sx={{
        zIndex: (theme) => theme.zIndex.drawer + 1,
        bgcolor: 'white'
      }}
    >
      <Toolbar className="gap-5">
        <Link
          className="flex text-2xl font-bold text-gray-900"
          to={routesBuilder.landingPage}
        >
          {t('title')}
        </Link>
        {user?.role === Role.ADMIN && <AdminPanelSettingsIcon />}
      </Toolbar>
    </AppBar>
  )
}
