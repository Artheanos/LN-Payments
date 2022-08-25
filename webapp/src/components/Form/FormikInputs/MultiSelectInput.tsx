import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  SelectChangeEvent,
  SelectProps
} from '@mui/material'
import React, { useState } from 'react'
import { FieldProps, useFormikContext } from 'formik'

interface Props extends FieldProps, SelectProps {
  options: string[]
}

export const MultiSelectInput: React.FC<Props> = ({
  field,
  form,
  className,
  options,
  label,
  ...other
}) => {
  const [display, setDisplay] = useState<string[]>([])

  const formikContext = useFormikContext<Record<string, string>>()
  const error = formikContext.errors[field.name]
  const touched = formikContext.touched[field.name]
  const showError = Boolean(error && touched)

  const labelId = `${field.name}-label-id`

  const onFieldChange = (event: SelectChangeEvent<typeof display>) => {
    const {
      target: { value }
    } = event
    form.setFieldValue(field.name, value)
    setDisplay(typeof value === 'string' ? value.split(',') : value)
  }

  return (
    <FormControl className={className}>
      <InputLabel id={labelId}>{label}</InputLabel>
      <Select
        error={showError}
        labelId={labelId}
        id={field.name}
        name={field.name}
        value={field.value}
        onChange={onFieldChange}
        input={<OutlinedInput label="Name" />}
        {...other}
      >
        {options.map((name: string) => (
          <MenuItem key={name} value={name}>
            {name}
          </MenuItem>
        ))}
      </Select>
      {showError && <FormHelperText error>{error}</FormHelperText>}
    </FormControl>
  )
}
