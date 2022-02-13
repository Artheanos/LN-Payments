import React from 'react'
import { AppBar } from '@mui/material'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'

export const Navbar: React.FC = () => {
  const { t } = useTranslation('common')

  return (
    <AppBar
      position={'static'}
      className="justify-start py-2 pl-10"
      color={'secondary'}
    >
      <Link
        className="flex text-2xl font-bold text-gray-900"
        to={routesBuilder.landingPage}
      >
        {t('title')}
      </Link>
    </AppBar>
  )
}
