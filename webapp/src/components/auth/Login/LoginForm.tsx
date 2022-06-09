import React from 'react'
import { Alert, Button, Grid } from '@mui/material'
import { Field, Form, Formik } from 'formik'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { CardForm, CardFormButton } from 'components/Form/CardForm'
import { LoginForm as ILoginForm } from 'webService/interface/auth'
import { TextInput } from 'components/Form/FormikInputs/TextInput'

type Props = {
  onSubmit: (form: ILoginForm) => void
  openAlert: boolean
  hideAlert: () => void
}

export const LoginForm: React.FC<Props> = ({
  onSubmit,
  openAlert,
  hideAlert
}) => {
  const { t } = useTranslation('auth')
  const initialValues: ILoginForm = { email: '', password: '' }

  return (
    <Formik initialValues={initialValues} onSubmit={onSubmit}>
      <Form>
        <CardForm
          title={t('login.header')}
          submitButtonContent={t('login.header')}
        >
          <Grid xs={12} item>
            <Field
              name="email"
              label={t('register.form.email.label')}
              variant="standard"
              className="w-full"
              component={TextInput}
            />
          </Grid>
          <Grid xs={12} item>
            <Field
              name="password"
              label={t('register.form.password.label')}
              type="password"
              variant="standard"
              className="w-full"
              component={TextInput}
            />
          </Grid>
          {openAlert ? (
            <Grid xs={12} item>
              <Alert onClose={hideAlert} variant="standard" severity="warning">
                {t('login.form.errorMessage')}
              </Alert>
            </Grid>
          ) : (
            <div className="w-1 h-4" />
          )}
          <Grid xs={12} item className="flex justify-between">
            <CardFormButton content={t('login.header')} />
            <Link to={routesBuilder.register}>
              <Button variant="text">{t('login.form.registerInstead')}</Button>
            </Link>
          </Grid>
        </CardForm>
      </Form>
    </Formik>
  )
}
