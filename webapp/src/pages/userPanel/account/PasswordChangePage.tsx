import React, { useState } from 'react'
import { useNotification } from 'components/Context/NotificationContext'
import Panel from 'components/common/Panel'
import { Field, Form, Formik, FormikHelpers } from 'formik'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { LoadingButton } from '@mui/lab'
import {
  PasswordChangeInitialValues,
  PasswordChangeProps,
  PasswordChangeSchema
} from './form'
import { api } from 'webService/requests'
import { useNavigate } from 'react-router-dom'
import routesBuilder from 'routesBuilder'
import { useTranslation } from 'react-i18next'
import { Grid } from '@mui/material'

export const PasswordChangePage: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const notification = useNotification()
  const navigate = useNavigate()
  const { t } = useTranslation('account')

  const changePassword = async (
    form: PasswordChangeProps,
    { setFieldError }: FormikHelpers<PasswordChangeProps>
  ) => {
    setLoading(true)
    const { status } = await api.users.changePassword(form)
    if (status === 200) {
      notification(t('passwordChange.success'), 'success')
      navigate(routesBuilder.userPanel.account.index)
    } else if (status === 409) {
      setFieldError('newPassword', t('passwordChange.error.409'))
    } else {
      notification(t('passwordChange.error.generic'), 'error')
      if (status === 400) {
        setFieldError('newPassword', t('common:error.message'))
      }
    }
    setLoading(false)
  }

  return (
    <Panel.Container>
      <Panel.Header title={t('passwordChange.header')} />
      <Panel.Body>
        <Formik
          initialValues={PasswordChangeInitialValues}
          onSubmit={changePassword}
          validationSchema={PasswordChangeSchema}
          enableReinitialize
        >
          <Form>
            <Grid container gap={3}>
              <Grid xs={12} item>
                <Field
                  name="currentPassword"
                  label={t('passwordChange.currentPassword')}
                  className="w-80"
                  component={TextInput}
                  type="password"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="newPassword"
                  label={t('passwordChange.newPassword')}
                  className="w-80"
                  component={TextInput}
                  type="password"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="newPasswordConfirmation"
                  label={t('passwordChange.confirmNewPassword')}
                  className="w-80"
                  component={TextInput}
                  type="password"
                />
              </Grid>
              <Grid xs={12} className="pt-10" item>
                <LoadingButton
                  type="submit"
                  variant="contained"
                  loading={loading}
                >
                  {t('common:submit')}
                </LoadingButton>
              </Grid>
            </Grid>
          </Form>
        </Formik>
      </Panel.Body>
    </Panel.Container>
  )
}
