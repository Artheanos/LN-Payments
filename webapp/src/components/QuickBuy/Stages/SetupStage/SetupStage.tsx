import React, { useCallback, useContext, useEffect, useState } from 'react'
import { Alert, Grid } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { Formik, Field, Form } from 'formik'

import { CardForm, CardFormButton } from 'components/Form/CardForm'
import { StageProps } from 'components/QuickBuy/StageProps'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { UserContext } from 'components/Context/UserContext'

import { Role } from 'webService/interface/user'
import { api } from 'webService/requests'
import { initialValues, PaymentLocalForm, schemaFactory } from './form'

export const SetupStage: React.FC<StageProps> = ({ onNext, setPayment }) => {
  const { t } = useTranslation('quickBuy')
  const { user, isLoggedIn, setToken, setUser } = useContext(UserContext)
  const [form, setForm] = useState<PaymentLocalForm>()
  const showEmailInput = !isLoggedIn || user?.role === Role.TEMPORARY

  const createPayment = useCallback(
    async (form: PaymentLocalForm) => {
      const { data } = await api.payment.create(form)
      if (data) {
        setPayment(data!)
        onNext()
      }
    },
    [onNext, setPayment]
  )

  const logInTemporaryUser = async (form: PaymentLocalForm) => {
    const response = await api.auth.temporary(form)
    if (response.data) {
      setToken(response.data.token)
      setUser({ email: form.email, role: Role.TEMPORARY, fullName: '' })
    }
  }

  const onSubmit = (form: PaymentLocalForm) => {
    if (!isLoggedIn) {
      logInTemporaryUser(form)
    }
    setForm(form)
  }

  useEffect(() => {
    if (form && isLoggedIn) {
      createPayment(form)
    }
  }, [isLoggedIn, form, createPayment])

  return (
    <Formik
      initialValues={initialValues(
        user?.role === Role.TEMPORARY ? user.email : ''
      )}
      onSubmit={onSubmit}
      validationSchema={schemaFactory(showEmailInput)}
    >
      <Form className="flex flex-col gap-10">
        <Alert variant="standard" severity="info" className="mx-auto">
          {t('setup.info')}
        </Alert>
        <CardForm className="mx-auto w-96" submitButtonContent="Next">
          <Grid xs={12} item>
            <Field
              name="numberOfTokens"
              label={t('setup.form.numberOfTokens.label')}
              type="number"
              variant="standard"
              InputProps={{ inputProps: { min: 1 } }}
              component={TextInput}
            />
          </Grid>

          {showEmailInput && (
            <Grid xs={12} item className="pb-5">
              <Field
                name="email"
                label={t('setup.form.email.label')}
                variant="standard"
                component={TextInput}
              />
            </Grid>
          )}

          <Grid xs={12} item className="flex justify-end">
            <CardFormButton content={t('common:next')} />
          </Grid>
        </CardForm>
      </Form>
    </Formik>
  )
}
