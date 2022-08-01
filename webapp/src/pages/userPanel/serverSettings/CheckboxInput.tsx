import React, { useEffect, useState } from 'react'
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  FormGroup
} from '@mui/material'
import { Field, useField } from 'formik'
import { TextInput } from 'components/Form/FormikInputs/TextInput'

type Props = {
  name: string
  label: string
  type: string
  checkboxLabel: string
}

const CheckboxInput: React.FC<Props> = ({
  label,
  name,
  type,
  checkboxLabel
}) => {
  const [show, setShow] = useState(false)
  const [field, , helpers] = useField(name)

  useEffect(() => {
    if (field.value) {
      setShow(true)
    }
  }, [field, show])

  function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
    const checked = event.target.checked
    if (!checked) {
      helpers.setValue('')
    }
    setShow(checked)
  }

  return (
    <FormControl>
      <FormGroup>
        <FormControlLabel
          control={
            <Checkbox
              checked={show}
              name={name + '-control'}
              onChange={handleChange}
            ></Checkbox>
          }
          label={checkboxLabel}
        />
        {show && (
          <Field
            name={name}
            label={label}
            className="w-80"
            component={TextInput}
            type={type}
          />
        )}
      </FormGroup>
    </FormControl>
  )
}

export default CheckboxInput
