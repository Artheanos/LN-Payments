import * as Yup from 'yup'

export const walletCreateSchema = Yup.object()
  .shape({
    adminEmails: Yup.array(Yup.string()).min(1),
    minSignatures: Yup.number()
      .positive()
      .integer()
      .required()
      .when('adminEmails', (adminEmails) => {
        return Yup.number().lessThan(
          adminEmails.length + 1,
          'Number of signatures must not be greater than the amount of admins'
        )
      })
  })
  .required()

export interface WalletForm {
  adminEmails: string[]
  minSignatures: number
}

export const initialValues = {
  adminEmails: [],
  minSignatures: ''
}
