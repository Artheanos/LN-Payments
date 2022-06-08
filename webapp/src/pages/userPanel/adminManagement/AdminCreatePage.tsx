import React, { useState } from 'react'
import { Field, Form, Formik, FormikHelpers } from 'formik'
import { Grid } from '@mui/material'

import Panel from 'components/common/Panel'
import { LoadingButton } from '@mui/lab'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import { api } from 'webService/requests'
import routesBuilder from 'routesBuilder'
import { useNotification } from 'components/Context/NotificationContext'
import {
  RegisterInitialValue,
  RegisterProps,
  RegisterSchema
} from 'components/auth/Register/form'

export const AdminCreatePage: React.FC = () => {
  const { t } = useTranslation('auth')
  const notification = useNotification()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)

  const onSubmit = async (
    form: RegisterProps,
    { setFieldError }: FormikHelpers<RegisterProps>
  ) => {
    setLoading(true)
    try {
      const { status } = await api.admins.create(form)
      if (status === 201) {
        notification('New admin has been created', 'success')
        navigate(routesBuilder.userPanel.admins.index)
      } else if (status === 409) {
        setFieldError('email', t('register.api.errors.409'))
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <Panel.Container>
      <Panel.Header title="Create Admin" />
      <Panel.Body>
        <Formik
          initialValues={RegisterInitialValue}
          onSubmit={onSubmit}
          validationSchema={RegisterSchema}
          initialStatus={{ email: '' }}
        >
          <Form>
            <Grid container gap={3}>
              <Grid xs={12} item>
                <Field
                  name="fullName"
                  label="Full name"
                  className="w-80"
                  component={TextInput}
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="email"
                  label="Email"
                  component={TextInput}
                  className="w-80"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="password"
                  label="Password"
                  component={TextInput}
                  type="password"
                  className="w-80"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="passwordConfirmation"
                  label={t('register.form.passwordConfirmation.label')}
                  type="password"
                  component={TextInput}
                  className="w-80"
                />
              </Grid>
              <Grid xs={12} className="pt-10" item>
                <LoadingButton
                  type="submit"
                  variant="contained"
                  loading={loading}
                >
                  Submit
                </LoadingButton>
              </Grid>
            </Grid>
          </Form>
        </Formik>
      </Panel.Body>
    </Panel.Container>
  )
}
