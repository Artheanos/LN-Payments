import React, { useEffect, useState } from 'react'
import { Alert, Button, CircularProgress, Grid } from '@mui/material'
import { Field, Form, Formik, FormikValues } from 'formik'

import Panel from 'components/common/Panel'
import { MultiSelectInput } from 'components/Form/FormikInputs/MultiSelectInput'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'webService/requests'
import {
  walletCreateSchema,
  initialValues
} from 'components/wallet/create/form'
import { useTranslation } from 'react-i18next'

type Props = {
  onSubmit: (values: FormikValues) => void
}

export const WalletCreatePage: React.FC<Props> = ({ onSubmit }) => {
  const { t } = useTranslation('wallet')
  const [admins, setAdmins] = useState<string[]>([])
  const [loading, setLoading] = useState(true)

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

  if (loading) return <CircularProgress />

  return (
    <Panel.Container>
      <Panel.Header title={t('createForm.header')} />
      <Panel.Body>
        <Formik
          initialValues={initialValues}
          onSubmit={onSubmit}
          validationSchema={walletCreateSchema}
        >
          <Form>
            <Grid container item gap={3} md={6}>
              {!admins.length ? (
                <Grid item xs={12}>
                  <Alert color="error">{t('createForm.noAdmins')}</Alert>
                </Grid>
              ) : (
                <>
                  <Grid item xs={12}>
                    <Field
                      name="minSignatures"
                      label={t('createForm.numSignatures')}
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
                      label={t('createForm.admins')}
                      multiple
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <Button type="submit" variant="contained">
                      {t('common:submit')}
                    </Button>
                  </Grid>
                </>
              )}
            </Grid>
          </Form>
        </Formik>
      </Panel.Body>
    </Panel.Container>
  )
}
