import React, { useEffect, useState } from 'react'
import { Field, Form, Formik } from 'formik'
import { CircularProgress, Grid } from '@mui/material'
import { LoadingButton } from '@mui/lab'

import Panel from 'components/common/Panel'
import { Settings } from 'webService/interface/settings'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'webService/requests'
import { serverSettingsSchema } from './form'
import { useNotification } from 'components/Context/NotificationContext'
import { useTranslation } from 'react-i18next'
import CheckboxInput from 'pages/userPanel/serverSettings/CheckboxInput'

export const ServerSettingsPage: React.FC = () => {
  const { t } = useTranslation('settings')

  const [settings, setSettings] = useState<Settings>()
  const [getting, setGetting] = useState(false)
  const [uploading, setUploading] = useState(false)
  const notification = useNotification()

  const getSettings = () => {
    setGetting(true)
    api.settings
      .getSettings()
      .then(({ data }) => {
        if (data) {
          setSettings(data)
        }
      })
      .finally(() => setGetting(false))
  }

  const updateSettings = (form: Settings) => {
    setUploading(true)
    api.settings
      .updateSettings(form)
      .then(({ status }) => {
        if (status === 200) {
          notification(t('form.success'), 'success')
          getSettings()
        } else {
          notification(t('common:error.generic'), 'error')
        }
      })
      .finally(() => setUploading(false))
  }

  useEffect(() => {
    getSettings()
  }, [])

  if (!settings) return <CircularProgress />

  return (
    <Panel.Container>
      <Panel.Header title={t('form.title')} />
      <Panel.Body>
        <Formik
          initialValues={settings}
          onSubmit={(form) => updateSettings(form)}
          validationSchema={serverSettingsSchema}
          enableReinitialize
        >
          <Form>
            <Grid container gap={3}>
              <Grid xs={12} item>
                <Field
                  name="price"
                  label={t('form.price.label')}
                  className="w-80"
                  component={TextInput}
                  type="number"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="description"
                  label={t('form.description.label')}
                  className="w-80"
                  component={TextInput}
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="invoiceMemo"
                  label={t('form.invoiceMemo.label')}
                  className="w-80"
                  component={TextInput}
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="paymentExpiryInSeconds"
                  label={t('form.paymentExpiryInSeconds.label')}
                  className="w-80"
                  component={TextInput}
                  type="number"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="autoChannelCloseLimit"
                  label={t('form.autoChannelCloseLimit.label')}
                  className="w-80"
                  component={TextInput}
                  type="number"
                />
              </Grid>
              <Grid xs={12} item>
                <Field
                  name="autoTransferLimit"
                  label={t('form.autoTransferLimit.label')}
                  className="w-80"
                  component={TextInput}
                  type="number"
                />
              </Grid>
              <Grid xs={12} item>
                <CheckboxInput
                  label={t('form.tokenDeliveryUrl.label')}
                  name="tokenDeliveryUrl"
                  type="string"
                  checkboxLabel={t('form.tokenDeliveryUrl.question')}
                />
              </Grid>
              <Grid xs={12} item>
                <CheckboxInput
                  label={t('form.serverIpAddress.label')}
                  name="serverIpAddress"
                  type="string"
                  checkboxLabel={t('form.serverIpAddress.question')}
                />
              </Grid>
              <Grid xs={12} item>
                <LoadingButton
                  type="submit"
                  variant="contained"
                  loading={uploading || getting}
                >
                  {t('common:submit')}
                </LoadingButton>
              </Grid>
            </Grid>
          </Form>
        </Formik>
      </Panel.Body>
    </Panel.Container>
  )
}
