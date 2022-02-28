import React, { ChangeEventHandler } from 'react'
import { FieldProps, FormikValues } from 'formik'
import { TextField, TextFieldProps } from '@mui/material'

export const TextInput: React.FC<
  FieldProps & TextFieldProps & FormikValues
> = ({ form, field, touched, error, ...other }) => {
  const onFieldChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    const newValue = event.target.value
    form.setFieldValue(field.name, newValue)
  }

  return (
    <TextField
      name={field.name}
      value={field.value}
      error={error && touched}
      helperText={error && touched && String(error)}
      onChange={onFieldChange}
      {...other}
    />
  )
}
