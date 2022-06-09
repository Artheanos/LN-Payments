import React, { useState } from 'react'
import { Button, Grid } from '@mui/material'
import { Field, Form, Formik, FormikHelpers } from 'formik'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { CardForm, CardFormButton } from 'components/Form/CardForm'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { Link, useNavigate } from 'react-router-dom'
import { RegisterInitialValue, RegisterProps, RegisterSchema } from './form'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'webService/requests'

export const Register: React.FC = () => {
  const { t } = useTranslation('auth')
  const navigate = useNavigate()
  const [showErrorsInFormModal, setShowErrorsInFormModal] = useState(false)
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const onSubmit = async (
    form: RegisterProps,
    { setFieldError }: FormikHelpers<RegisterProps>
  ) => {
    const { status } = await api.auth.register(form)
    if (status === 201) {
      setShowSuccessModal(true)
    } else if (status === 409) {
      setFieldError('email', t('register.api.errors.409'))
    } else {
      if (status === 400) {
        setErrorMessage('register.api.errors.400')
      } else {
        setErrorMessage('register.api.errors.default')
      }
      setShowErrorsInFormModal(true)
    }
  }

  return (
    <div className="mx-auto mt-20 w-96">
      <Formik
        initialValues={RegisterInitialValue}
        onSubmit={onSubmit}
        validationSchema={RegisterSchema}
        initialStatus={{ email: '' }}
      >
        <Form>
          <CardForm
            title={t('register.header')}
            submitButtonContent={t('register.send')}
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
                name="fullName"
                label={t('register.form.fullName.label')}
                variant="standard"
                className="w-full"
                component={TextInput}
              />
            </Grid>
            <Grid xs={12} item>
              <Field
                name="password"
                type="password"
                label={t('register.form.password.label')}
                variant="standard"
                className="w-full"
                component={TextInput}
              />
            </Grid>
            <Grid xs={12} item className="pb-4">
              <Field
                name="passwordConfirmation"
                type="password"
                label={t('register.form.passwordConfirmation.label')}
                variant="standard"
                className="w-full"
                component={TextInput}
              />
            </Grid>
            <Grid xs={12} item className="flex justify-between">
              <CardFormButton content={t('register.send')} />
              <Link to={routesBuilder.login}>
                <Button variant="text">Login instead</Button>
              </Link>
            </Grid>
          </CardForm>
        </Form>
      </Formik>
      <ConfirmationModal
        confirmButtonContent={t('common:ok')}
        message={t(errorMessage)}
        open={showErrorsInFormModal}
        setOpen={setShowErrorsInFormModal}
      />
      <ConfirmationModal
        confirmButtonContent={t('common:ok')}
        message={t('register.api.success.message')}
        onConfirm={() => navigate(routesBuilder.login)}
        open={showSuccessModal}
        setOpen={setShowSuccessModal}
      />
    </div>
  )
}
