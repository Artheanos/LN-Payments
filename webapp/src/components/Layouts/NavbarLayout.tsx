import React, { useContext } from 'react'
import { Outlet } from 'react-router-dom'

import { Navbar } from 'components/common/Navbar'
import { Alert, Box, CircularProgress, Snackbar, Toolbar } from '@mui/material'
import { UserContext } from 'components/Context/UserContext'
import { useTranslation } from 'react-i18next'

export const NavbarLayout: React.FC = () => {
  const { t } = useTranslation('common')
  const { loading } = useContext(UserContext)

  return (
    <Box>
      <Navbar />
      <div>
        <Toolbar />
        <Outlet />
        {loading ? (
          <Snackbar open>
            <Alert severity="info" className="flex items-center">
              <div className="flex gap-5 items-center">
                <div>{t('refreshingToken')}</div>
                <CircularProgress color="info" />
              </div>
            </Alert>
          </Snackbar>
        ) : null}
      </div>
    </Box>
  )
}
