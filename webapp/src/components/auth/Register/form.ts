import * as Yup from 'yup'
import { t } from 'i18next'
import { isValidBitcoinAddress } from 'bitcoin-address-soft-regex-validation'

export const RegisterSchema = Yup.object()
  .shape({
    email: Yup.string()
      .email(t('auth:register.form.email.errors.valid'))
      .required(t('auth:register.form.email.errors.required')),
    fullName: Yup.string()
      .min(3, t('auth:register.form.fullName.errors.min'))
      .max(50, t('auth:register.form.fullName.errors.max'))
      .matches(
        /^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆŠŽ∂ð ,.'-]+$/,
        t('auth:register.form.fullName.errors.specialCharacters')
      )
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

export const TransactionSchema = Yup.object().shape({
  amount: Yup.number()
    .min(1, 'Amount must be larger that 1')
    .required('Amount must not be null'),
  targetAddress: Yup.string()
    .required('Address is required')
    .test(
      'Validate bitcoin address',
      'Value is not a bitcoin address',
      (value) =>
        ['mainnet', 'testnet'].includes(
          <string>isValidBitcoinAddress(value as string)
        )
    )
})

export type TransactionProps = typeof TransactionInitialValue

export const TransactionInitialValue = {
  amount: 0,
  targetAddress: ''
}
