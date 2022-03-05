import React, { useContext, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { LoginForm } from 'components/auth/Login/LoginForm'
import { api } from 'api'
import { useNotification } from 'components/Context/NotificationContext'
import { UserContext } from 'components/Context/UserContext'

export const LoginPage: React.FC = () => {
  const { t } = useTranslation('auth')
  const { setUser, setToken } = useContext(UserContext)
  const navigate = useNavigate()
  const createSnackbar = useNotification()
  const [openAlert, setOpenAlert] = useState(false)

  const onSubmit = async (form: LoginForm) => {
    setOpenAlert(false)
    const { data } = await api.auth.login(form)

    if (data) {
      setUser(data)
      setToken(data.token)
      createSnackbar(t('login.form.successMessage'), 'success', 5000)
      navigate(routesBuilder.landingPage)
    } else {
      setOpenAlert(true)
    }
  }

  return (
    <div className="mx-auto mt-20 w-96">
      <LoginForm
        onSubmit={onSubmit}
        openAlert={openAlert}
        hideAlert={() => setOpenAlert(false)}
      />
    </div>
  )
}
