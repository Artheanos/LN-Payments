import React from 'react'
import { useTranslation } from 'react-i18next'
import { Field, Form, Formik } from 'formik'
import { TextInput } from '../Form/FormikInputs/TextInput'
import { Button } from '@mui/material'
import * as Yup from 'yup'
import { CheckboxInput } from '../Form/FormikInputs/CheckboxInput'

const RegisterSchema = Yup.object().shape({
  email: Yup.string().email().required('Email is required'),
  fullName: Yup.string()
    .min(2, 'Too Short!')
    .max(50, 'Too Long!')
    .required('chuj Required'),
  password: Yup.string().min(8, '').required(),
  passwordConfirmation: Yup.string().oneOf([Yup.ref('password')], ''),
  termsConfirmation: Yup.boolean().required()
})

type RegisterProps = Yup.InferType<typeof RegisterSchema>

export const Register: React.FC = () => {
  const { t } = useTranslation('common')

  const onSubmit = (form: RegisterProps) => {
    console.log(form)
  }

  return (
    <div className="mt-12 w-auto text-center">
      <div>{t('register.header')}</div>
      <Formik
        initialValues={{
          email: '',
          fullName: '',
          password: '',
          passwordConfirmation: '',
          termsConfirmation: false
        }}
        onSubmit={onSubmit}
        validationSchema={RegisterSchema}
      >
        {({ errors, touched }) => (
          <Form className="flex flex-col gap-4 mx-auto mt-12 w-64">
            <Field
              name="email"
              label={t('quickBuy.setup.form.tokenCount.label')}
              variant="outlined"
              component={TextInput}
            />
            <Field
              name="fullName"
              label={t('quickBuy.setup.form.email.label')}
              variant="outlined"
              error={Boolean(errors.fullName && touched.fullName)}
              helperText={
                errors.fullName && touched.fullName && String(errors.fullName)
              }
              component={TextInput}
            />
            <Field
              name="password"
              label={t('quickBuy.setup.form.tokenCount.label')}
              variant="outlined"
              component={TextInput}
            />
            <Field
              name="passwordConfirmation"
              label={t('quickBuy.setup.form.tokenCount.label')}
              variant="outlined"
              component={TextInput}
            />
            <Field
              name="termsConfirmation"
              label={t('quickBuy.setup.form.tokenCount.label')}
              component={CheckboxInput}
            />
            <div className="flex justify-end">
              <Button
                variant="contained"
                className="bg-purple-300"
                type="submit"
              >
                {t('next')}
              </Button>
            </div>
          </Form>
        )}
      </Formik>
    </div>
  )
}
