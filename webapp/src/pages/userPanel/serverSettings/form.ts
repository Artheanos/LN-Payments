import * as Yup from 'yup'
import { t } from 'i18next'

export const serverSettingsSchema = Yup.object()
  .shape({
    autoChannelCloseLimit: Yup.number()
      .label(t('settings:form.autoChannelCloseLimit.label'))
      .required()
      .min(100),
    autoTransferLimit: Yup.number()
      .label(t('settings:form.autoTransferLimit.label'))
      .required()
      .min(100),
    description: Yup.string()
      .label(t('settings:form.description.label'))
      .required()
      .min(10)
      .max(256),
    invoiceMemo: Yup.string()
      .label(t('settings:form.invoiceMemo.label'))
      .required()
      .min(10)
      .max(128),
    paymentExpiryInSeconds: Yup.number()
      .label(t('settings:form.paymentExpiryInSeconds.label'))
      .required()
      .min(300)
      .max(3600),
    price: Yup.number().label(t('settings:form.price.label')).required().min(1)
  })
  .required()
