import React, { useContext, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { LocalKey } from 'constants/LocalKey'
import { LoginForm as ILoginForm } from 'webService/interface/auth'
import { LoginForm } from 'components/auth/Login/LoginForm'
import { UserContext } from 'components/Context/UserContext'
import { api } from 'webService/requests'
import { useNotification } from 'components/Context/NotificationContext'

export const LoginPage: React.FC = () => {
  const { t } = useTranslation('auth')
  const { login } = useContext(UserContext)
  const navigate = useNavigate()
  const createSnackbar = useNotification()
  const [openAlert, setOpenAlert] = useState(false)

  const onSubmit = async (form: ILoginForm) => {
    setOpenAlert(false)
    const { data } = await api.auth.login(form)

    if (data) {
      localStorage.setItem(LocalKey.TRANSACTION_TOKENS, '')
      login(data, data.token)
      createSnackbar(t('login.form.successMessage'), 'success')
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
