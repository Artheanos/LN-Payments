import * as Yup from 'yup'
import { t } from 'i18next'
import { PaymentForm } from 'webService/interface/payment'

export const initialValues = (email = ''): PaymentForm => ({
  email,
  numberOfTokens: 1
})

export const schema = Yup.object()
  .shape({
    email: Yup.string()
      .label(t('quickBuy:setup.form.email.label'))
      .email()
      .required(),
    numberOfTokens: Yup.number()
      .label(t('quickBuy:setup.form.numberOfTokens.label'))
      .min(1)
  })
  .required()
