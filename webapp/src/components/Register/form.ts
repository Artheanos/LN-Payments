import * as Yup from 'yup'
import i18next from 'i18next'

const { t } = i18next

export const RegisterSchema = Yup.object()
  .shape({
    email: Yup.string()
      .email(t('register.form.email.errors.valid'))
      .required(t('register.form.email.errors.required')),
    fullName: Yup.string()
      .min(3, t('register.form.fullName.errors.min'))
      .max(50, t('register.form.fullName.errors.max'))
      .required(t('register.form.fullName.errors.required')),
    password: Yup.string()
      .matches(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})/,
        t('register.form.password.errors.regex')
      )
      .required(t('register.form.password.errors.required')),
    passwordConfirmation: Yup.string()
      .oneOf(
        [Yup.ref('password')],
        t('register.form.passwordConfirmation.errors.match')
      )
      .required(t('register.form.passwordConfirmation.errors.match'))
  })
  .required()

export type RegisterProps = Yup.InferType<typeof RegisterSchema>

export const RegisterInitialValue: RegisterProps = {
  email: '',
  fullName: '',
  password: '',
  passwordConfirmation: ''
}
