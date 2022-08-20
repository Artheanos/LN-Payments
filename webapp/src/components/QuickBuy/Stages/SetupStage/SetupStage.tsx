import React, { useContext } from 'react'
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

export const SetupStage: React.FC<StageProps> = ({
  onNext,
  setPayment,
  paymentInfo
}) => {
  const { t } = useTranslation('quickBuy')
  const { user, isLoggedIn, hasAccount, login } = useContext(UserContext)

  const createPayment = async (form: PaymentLocalForm) => {
    const { data } = await api.payment.create(form)
    if (data) {
      setPayment(data!)
      onNext()
    }
  }

  const logInTemporaryUser = async (form: PaymentLocalForm) => {
    const response = await api.auth.temporary({ email: form.email })
    if (response.data) {
      login(
        {
          email: form.email,
          role: Role.TEMPORARY,
          fullName: ''
        },
        response.data.token
      )
      await createPayment(form)
    }
  }

  const onSubmit = (form: PaymentLocalForm) => {
    if (isLoggedIn) {
      createPayment(form)
    } else {
      logInTemporaryUser(form)
    }
  }

  return (
    <Formik
      initialValues={initialValues(
        user?.role === Role.TEMPORARY ? user.email : ''
      )}
      onSubmit={onSubmit}
      validationSchema={schemaFactory(!hasAccount)}
    >
      {({ values }) => (
        <Form className="flex flex-col gap-10">
          <Alert variant="standard" severity="info" className="mx-auto">
            {t(
              `setup.${hasAccount ? 'infoWithAccount' : 'infoWithoutAccount'}`
            )}
          </Alert>
          <CardForm className="mx-auto w-96" submitButtonContent="Next">
            <Grid xs={12} item>
              <DisplayField
                label={t('setup.form.singleTokenPrice')}
                value={t('withSat', paymentInfo)}
              />
            </Grid>

            <Grid xs={12} item>
              <DisplayField
                label={t('setup.form.description')}
                value={paymentInfo.description}
              />
            </Grid>

            <Grid xs={12} item>
              <DisplayField
                label="Total"
                value={t('withSat', {
                  price: paymentInfo.price * values.numberOfTokens
                })}
              />
            </Grid>

            <Grid xs={12} item>
              <hr />
            </Grid>

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

            {!hasAccount && (
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
      )}
    </Formik>
  )
}

const DisplayField = (props: { value: string; label: string }) => (
  <div>
    <b>{props.label}:</b> {props.value}
  </div>
)
