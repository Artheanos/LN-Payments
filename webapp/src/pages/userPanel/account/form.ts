import * as Yup from 'yup'
import { t } from 'i18next'

export const PasswordChangeSchema = Yup.object()
  .shape({
    currentPassword: Yup.string().required(
      t('account:passwordChange.validation.currentRequired')
    ),
    newPassword: Yup.string()
      .matches(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
        t('auth:register.form.password.errors.regex')
      )
      .notOneOf(
        [Yup.ref('currentPassword')],
        t('account:passwordChange.validation.reuse')
      )
      .required(t('account:passwordChange.validation.newRequired')),
    newPasswordConfirmation: Yup.string()
      .oneOf(
        [Yup.ref('newPassword')],
        t('auth:register.form.passwordConfirmation.errors.match')
      )
      .required(t('auth:register.form.passwordConfirmation.errors.match'))
  })
  .required()

export type PasswordChangeProps = typeof PasswordChangeInitialValues

export const PasswordChangeInitialValues = {
  currentPassword: '',
  newPassword: '',
  newPasswordConfirmation: ''
}
