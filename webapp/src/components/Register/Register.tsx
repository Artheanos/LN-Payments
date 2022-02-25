import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Field, Form, Formik } from 'formik'
import { TextInput } from '../Form/FormikInputs/TextInput'
import { Button } from '@mui/material'
import * as Yup from 'yup'
import { ConfirmationModal } from '../Modals/ConfirmationModal'
import { api } from '../../api'

export const Register: React.FC = () => {
  const { t } = useTranslation('common')
  const [showErrorsInFormModal, setShowErrorsInFormModal] =
    useState<boolean>(false)
  const [showSuccessModal, setShowSuccessModal] = useState<boolean>(false)
  const [errorMessage, setErrorMessage] = useState<string>('')

  const RegisterSchema = Yup.object()
    .shape({
      email: Yup.string()
        .email(t('register.form.email.errors.valid'))
        .required(t('register.form.email.errors.required')),
      fullName: Yup.string()
        .min(3, t('register.form.fullName.errors.min'))
        .max(50, t('register.form.fullName.errors.max'))
        .required(t('register.form.fullName.errors.required')),
      password: Yup.string()
        .matches(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
          t('register.form.password.errors.regex')
        )
        .required(t('register.form.password.errors.required')),
      passwordConfirmation: Yup.string()
        .oneOf(
          [Yup.ref('password')],
          t('register.form.passwordConfirmation.errors.match')
        )
        .required(t('register.form.passwordConfirmation.errors.match'))
    })
    .required()
  type RegisterProps = Yup.InferType<typeof RegisterSchema>

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
        initialValues={{
          email: '',
          fullName: '',
          password: '',
          passwordConfirmation: ''
        }}
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
