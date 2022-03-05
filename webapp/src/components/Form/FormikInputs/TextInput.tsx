import React, { ChangeEventHandler } from 'react'
import { FieldProps, FormikValues, useFormikContext } from 'formik'
import { TextField, TextFieldProps } from '@mui/material'

type Props = FieldProps & TextFieldProps & FormikValues

export const TextInput: React.FC<Props> = ({ form, field, ...other }) => {
  const onFieldChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    const newValue = event.target.value
    form.setFieldValue(field.name, newValue)
  }
  const formikContext = useFormikContext<Record<string, string>>()
  const error = formikContext.errors[field.name]
  const touched = formikContext.touched[field.name]

  return (
    <TextField
      name={field.name}
      value={field.value}
      error={Boolean(error && touched)}
      helperText={touched && error}
      onChange={onFieldChange}
      {...other}
    />
  )
}
