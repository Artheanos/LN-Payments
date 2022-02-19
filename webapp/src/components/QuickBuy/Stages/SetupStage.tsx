import React from 'react'
import { Alert, Grid } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { Formik, Field, Form } from 'formik'

import { CardForm, CardFormButton } from 'components/Form/CardForm'
import { StageProps } from 'components/QuickBuy/StageProps'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'api'

export const SetupStage: React.FC<StageProps> = ({ onNext, setPayment }) => {
  const { t } = useTranslation('common')

  const onSubmit = async (form: PaymentForm) => {
    const { data } = await api.payment.create(form)
    if (data) {
      setPayment(data!)
      onNext()
    }
  }

  const initialValues = { email: '', numberOfTokens: 1 }

  return (
    <Formik initialValues={initialValues} onSubmit={onSubmit}>
      <Form className="flex flex-col gap-10">
        <Alert variant="standard" severity="info" className="mx-auto">
          {t('quickBuy.setup.info')}
        </Alert>
        <CardForm className="mx-auto w-96" submitButtonContent="Next">
          <Grid xs={12} item>
            <Field
              name="numberOfTokens"
              label={t('quickBuy.setup.form.tokenCount.label')}
              type="number"
              variant="standard"
              component={TextInput}
            />
          </Grid>

          <Grid xs={12} item className="pb-5">
            <Field
              name="email"
              label={t('quickBuy.setup.form.email.label')}
              variant="standard"
              component={TextInput}
            />
          </Grid>

          <Grid xs={12} item className="flex justify-end">
            <CardFormButton content={t('next')} />
          </Grid>
        </CardForm>
      </Form>
    </Formik>
  )
}
