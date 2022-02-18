import React, { ChangeEventHandler } from 'react'
import { FieldProps } from 'formik'
import { TextField, TextFieldProps } from '@mui/material'

export const TextInput: React.FC<FieldProps & TextFieldProps> = ({
  form,
  field,
  ...other
}) => {
  const onFieldChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    const newValue = event.target.value
    form.setFieldValue(field.name, newValue)
  }

  return (
    <TextField
      name={field.name}
      value={field.value}
      onChange={onFieldChange}
      {...other}
    />
  )
}
