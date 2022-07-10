import React, { useContext } from 'react'
import { Alert, Grid } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { Formik, Field, Form } from 'formik'

import { CardForm, CardFormButton } from 'components/Form/CardForm'
import { PaymentForm } from 'webService/interface/payment'
import { StageProps } from 'components/QuickBuy/StageProps'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { UserContext } from 'components/Context/UserContext'
import { api } from 'webService/requests'
import { initialValues, schema } from './form'

export const SetupStage: React.FC<StageProps> = ({ onNext, setPayment }) => {
  const { t } = useTranslation('quickBuy')
  const { user } = useContext(UserContext)

  const onSubmit = async (form: PaymentForm) => {
    const { data } = await api.payment.create(form)
    if (data) {
      setPayment(data!)
      onNext()
    }
  }

  return (
    <Formik
      initialValues={initialValues(user?.email)}
      onSubmit={onSubmit}
      validationSchema={schema}
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
              component={TextInput}
            />
          </Grid>

          <Grid xs={12} item className="pb-5">
            <Field
              name="email"
              label={t('setup.form.email.label')}
              variant="standard"
              component={TextInput}
            />
          </Grid>

          <Grid xs={12} item className="flex justify-end">
            <CardFormButton content={t('common:next')} />
          </Grid>
        </CardForm>
      </Form>
    </Formik>
  )
}
