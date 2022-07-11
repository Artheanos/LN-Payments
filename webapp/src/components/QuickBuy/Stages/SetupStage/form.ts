import * as Yup from 'yup'
import { t } from 'i18next'
import { ObjectShape } from 'yup/lib/object'

import { PaymentForm } from 'webService/interface/payment'

export interface PaymentLocalForm extends PaymentForm {
  email: string
}

export const initialValues = (email: string): PaymentLocalForm => ({
  email,
  numberOfTokens: 1
})

export const schemaFactory = (showEmail: boolean) => {
  const shape: ObjectShape = {
    numberOfTokens: Yup.number()
      .label(t('quickBuy:setup.form.numberOfTokens.label'))
      .min(1)
  }

  if (showEmail) {
    shape.email = Yup.string()
      .label(t('quickBuy:setup.form.email.label'))
      .email()
      .required()
  }

  return Yup.object().shape(shape).required()
}
