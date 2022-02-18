import React from 'react'
import { Button } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { Formik, Field, Form } from 'formik'

import { StageProps } from 'components/QuickBuy/StageProps'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'api'

interface CreateForm {
  numberOfTokens: number
  email: string
}

export const SetupStage: React.FC<StageProps> = ({ onNext, setPayment }) => {
  const { t } = useTranslation('common')

  const onSubmit = (form: CreateForm) => {
    api.payment.create(form).then((payment) => {
      setPayment(payment)
      onNext()
    })
  }

  return (
    <div>
      <div>{t('quickBuy.setup.info')}</div>
      <Formik
        initialValues={{ numberOfTokens: 1, email: '' }}
        onSubmit={onSubmit}
      >
        <Form className="flex flex-col gap-4 mx-auto mt-12 w-64">
          <Field
            name="numberOfTokens"
            label={t('quickBuy.setup.form.tokenCount.label')}
            type="number"
            variant="outlined"
            component={TextInput}
          />
          <Field
            name="email"
            label={t('quickBuy.setup.form.email.label')}
            variant="outlined"
            component={TextInput}
          />
          <div className="flex justify-end">
            <Button variant="contained" className="bg-purple-300" type="submit">
              {t('next')}
            </Button>
          </div>
        </Form>
      </Formik>
    </div>
  )
}
