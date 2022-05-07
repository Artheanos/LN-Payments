import React, { useEffect, useState } from 'react'
import { Alert, Button, CircularProgress, Grid } from '@mui/material'
import { Field, Form, Formik, FormikValues } from 'formik'
import { useNavigate } from 'react-router-dom'

import Panel from 'components/common/Panel'
import { MultiSelectInput } from 'components/Form/FormikInputs/MultiSelectInput'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'api'
import {
  walletCreateSchema,
  initialValues,
  WalletForm
} from 'components/wallet/create/form'
import routesBuilder from 'routesBuilder'

export const WalletCreatePage: React.FC = () => {
  const [admins, setAdmins] = useState<string[]>([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    api.admins
      .getAll()
      .then(({ data }) => {
        if (data) {
          setAdmins(
            data.content.filter((user) => user.hasKey).map((user) => user.email)
          )
        }
      })
      .finally(() => setLoading(false))
  }, [])

  const onSubmit = async (values: FormikValues) => {
    const { status } = await api.wallet.create(values as WalletForm)

    if (status === 201) {
      navigate(routesBuilder.userPanel.wallet.index)
    } else {
      alert('Error')
    }
  }

  if (loading) return <CircularProgress />

  return (
    <Panel.Container>
      <Panel.Header title="Create wallet" />
      <Panel.Body>
        <Formik
          initialValues={initialValues}
          onSubmit={onSubmit}
          validationSchema={walletCreateSchema}
        >
          <Form>
            <Grid container item gap={3} md={6}>
              <Grid item xs={12}>
                <Field
                  name="minSignatures"
                  label="Minimum number of signatures"
                  component={TextInput}
                  className="w-full"
                  type="number"
                />
              </Grid>
              <Grid item xs={12}>
                <Field
                  name="adminEmails"
                  options={admins}
                  component={MultiSelectInput}
                  className="w-full"
                  label="Admins"
                  multiple
                />
                {!admins.length && (
                  <Alert color="error">
                    There are no admins with assigned keys
                  </Alert>
                )}
              </Grid>
              <Grid item xs={12}>
                <Button type="submit" variant="contained">
                  Submit
                </Button>
              </Grid>
            </Grid>
          </Form>
        </Formik>
      </Panel.Body>
    </Panel.Container>
  )
}
