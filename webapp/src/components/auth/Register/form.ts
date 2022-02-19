import * as Yup from 'yup'
import i18next from 'i18next'

const { t } = i18next

export const RegisterSchema = Yup.object()
  .shape({
    email: Yup.string()
      .email(t('auth:register.form.email.errors.valid'))
      .required(t('auth:register.form.email.errors.required')),
    fullName: Yup.string()
      .min(3, t('auth:register.form.fullName.errors.min'))
      .max(50, t('auth:register.form.fullName.errors.max'))
      .required(t('auth:register.form.fullName.errors.required')),
    password: Yup.string()
      .matches(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
        t('auth:register.form.password.errors.regex')
      )
      .required(t('auth:register.form.password.errors.required')),
    passwordConfirmation: Yup.string()
      .oneOf(
        [Yup.ref('password')],
        t('auth:register.form.passwordConfirmation.errors.match')
      )
      .required(t('auth:register.form.passwordConfirmation.errors.match'))
  })
  .required()

export type RegisterProps = typeof RegisterInitialValue

export const RegisterInitialValue = {
  email: '',
  fullName: '',
  password: '',
  passwordConfirmation: ''
}
