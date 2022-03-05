import { Card, Grid } from '@mui/material'
import React, { HTMLProps } from 'react'
import { LoadingButton } from '@mui/lab'
import { useFormikContext } from 'formik'

type Props = {
  title?: string
  submitButtonContent?: string
} & Pick<HTMLProps<HTMLDivElement>, 'className'>

export const CardFormButton: React.FC<{ content: string }> = ({ content }) => {
  const { isSubmitting } = useFormikContext()

  return (
    <LoadingButton type="submit" variant="contained" loading={isSubmitting}>
      {content}
    </LoadingButton>
  )
}

export const CardForm: React.FC<Props> = ({ title, children, className }) => {
  return (
    <Card
      className={`p-12 rounded-2xl ${className}`}
      sx={{ borderRadius: '1rem' }}
    >
      <Grid container spacing={4}>
        {title && (
          <Grid item>
            <h1 className="text-2xl font-bold">{title}</h1>
          </Grid>
        )}
        {children}
      </Grid>
    </Card>
  )
}
