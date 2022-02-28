import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Field, Form, Formik } from 'formik'
import { api } from '../../api'
import { RegisterInitialValue, RegisterProps, RegisterSchema } from './form'
import { TextInput } from '../Form/FormikInputs/TextInput'
import { Button } from '@mui/material'
import { ConfirmationModal } from '../Modals/ConfirmationModal'

export const Register: React.FC = () => {
  const { t } = useTranslation('common')
  const [showErrorsInFormModal, setShowErrorsInFormModal] =
    useState<boolean>(false)
  const [showSuccessModal, setShowSuccessModal] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const onSubmit = (form: RegisterProps) => {
    api.register
      .create(form)
      .then(() => {
        //TODO redirect to login page on confirm? LP-76
        setShowSuccessModal(true)
      })
      .catch((error) => {
        switch (error.response.status) {
          case 409:
            setErrorMessage('register.api.errors.409')
            break
          case 400:
            setErrorMessage('register.api.errors.400')
            break
          default:
            setErrorMessage('register.api.errors.default')
            break
        }
        setShowErrorsInFormModal(true)
      })
  }

  return (
    <div className="mt-12 w-auto text-center">
      <div className={'mt-0 mb-2 text-4xl font-medium leading-tight'}>
        {t('register.header')}
      </div>
      <Formik
        initialValues={RegisterInitialValue}
        onSubmit={onSubmit}
        validationSchema={RegisterSchema}
      >
        {({ errors, touched }) => (
          <Form className="flex flex-col gap-4 mx-auto mt-8 w-64">
            <Field
              name="email"
              label={t('register.form.email.label')}
              variant="outlined"
              touched={touched.email}
              error={errors.email}
              component={TextInput}
            />
            <Field
              name="fullName"
              label={t('register.form.fullName.label')}
              variant="outlined"
              touched={touched.fullName}
              error={errors.fullName}
              component={TextInput}
            />
            <Field
              name="password"
              type={'password'}
              label={t('register.form.password.label')}
              touched={touched.password}
              error={errors.password}
              variant="outlined"
              component={TextInput}
            />
            <Field
              name="passwordConfirmation"
              type={'password'}
              label={t('register.form.passwordConfirmation.label')}
              touched={touched.passwordConfirmation}
              error={errors.passwordConfirmation}
              variant="outlined"
              component={TextInput}
            />
            <div className="flex justify-end">
              <Button
                fullWidth
                variant="contained"
                className="bg-purple-300"
                type="submit"
              >
                {t('register.send')}
              </Button>
            </div>
          </Form>
        )}
      </Formik>
      <ConfirmationModal
        confirmButtonContent={'Ok'}
        message={t(errorMessage)}
        onConfirm={() => setShowErrorsInFormModal(false)}
        open={showErrorsInFormModal}
        setOpen={setShowErrorsInFormModal}
      />
      <ConfirmationModal
        confirmButtonContent={'Ok'}
        message={t('register.api.success.message')}
        onConfirm={() => setShowSuccessModal(false)}
        open={showSuccessModal}
        setOpen={setShowSuccessModal}
      />
    </div>
  )
}
