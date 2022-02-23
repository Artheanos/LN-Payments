import React, { ChangeEventHandler } from 'react'
import { FieldProps } from 'formik'
import { Checkbox, RadioProps } from '@mui/material'

export const CheckboxInput: React.FC<FieldProps & RadioProps> = ({
  form,
  field,
  ...other
}) => {
  const onFieldChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    const newValue = event.target.value
    form.setFieldValue(field.name, newValue)
  }

  return (
    <Checkbox
      name={field.name}
      value={field.value}
      onChange={onFieldChange}
      {...other}
    />
  )
}
