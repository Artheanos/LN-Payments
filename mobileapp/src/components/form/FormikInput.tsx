import React from 'react'
import { FormControl, Input } from 'native-base'
import { FieldProps } from 'formik'
import { InterfaceInputProps } from 'native-base/lib/typescript/components/primitives/Input/types'
import { capitalize } from 'utils/strings'

type Props = FieldProps & InterfaceInputProps

/**
 * Component for standard text input that is integrated with formik. Includes all necessary methods, styling
 * and error handling.
 *
 * @param props  Formik input properties {@see FieldProps}
 */
export const FormikInput: React.FC<Props> = (props) => {
  const {
    field: { name, onBlur, onChange, value },
    form: { errors, touched, setFieldTouched },
    placeholder,
    ...inputProps
  } = props

  const hasError = Boolean(errors[name] && touched[name])

  return (
    <FormControl isInvalid={hasError}>
      <Input
        value={value}
        placeholder={placeholder || capitalize(name)}
        onChangeText={(text) => onChange(name)(text)}
        onBlur={() => {
          setFieldTouched(name)
          onBlur(name)
        }}
        {...inputProps}
      />
      <FormControl.ErrorMessage pl={3}>{errors[name]}</FormControl.ErrorMessage>
    </FormControl>
  )
}
