import React, { useCallback, useEffect, useState } from 'react'
import { Alert, CircularProgress, Grid } from '@mui/material'
import { Field, Form, Formik, FormikHelpers } from 'formik'
import { LoadingButton } from '@mui/lab'
import { Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import Panel from 'components/common/Panel'
import routesBuilder from 'routesBuilder'
import { NewTransactionInfo } from 'webService/interface/transaction'
import { TextInput } from 'components/Form/FormikInputs/TextInput'
import { api } from 'webService/requests'
import { useNotification } from 'components/Context/NotificationContext'
import {
  TransactionInitialValue,
  TransactionProps,
  TransactionSchema
} from 'components/auth/Register/form'

export const TransactionFormPage: React.FC = () => {
  const [loading, setLoading] = useState(true)
  const [info, setInfo] = useState<NewTransactionInfo>()
  const notification = useNotification()
  const navigate = useNavigate()
  const { t } = useTranslation('transactions')

  const getInfo = useCallback(async () => {
    setLoading(true)
    const { data } = await api.transactions.getNewTransactionInfo()
    if (data) {
      setInfo(data)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    getInfo()
  }, [getInfo])

  const onSubmit = async (
    form: TransactionProps,
    { setFieldError }: FormikHelpers<TransactionProps>
  ) => {
    if (form.amount! > info!.bitcoinWalletBalance.availableBalance) {
      setFieldError('amount', t('onSubmit.wrongBalance'))
      return
    }
    const { status } = await api.transactions.createTransaction(form)
    if (status === 201) {
      notification(t('onSubmit.on200'), 'success')
      navigate(routesBuilder.userPanel.transactions.index)
    } else if (status === 400) {
      setFieldError('amount', t('onSubmit.on400'))
    } else {
      notification(t('onSubmit.onOthers'), 'error')
    }
  }

  if (!loading && info?.pendingExists) {
    return <Navigate to={routesBuilder.userPanel.transactions.index} />
  }

  return (
    <Panel.Container>
      <Panel.Header title={t('createHeader')} />
      <Panel.Body>
        {loading ? (
          <div className="text-center">
            <CircularProgress />
          </div>
        ) : (
          <Formik
            initialValues={TransactionInitialValue}
            onSubmit={onSubmit}
            validationSchema={TransactionSchema}
          >
            <Form>
              <Grid container>
                <Grid item container xs={6} gap={5}>
                  <Grid item xs={10}>
                    <Field
                      name="amount"
                      label={t('amount')}
                      className="w-full"
                      component={TextInput}
                    />
                  </Grid>
                  <Grid item xs={10}>
                    <Field
                      name="targetAddress"
                      label={t('address')}
                      className="w-full"
                      component={TextInput}
                    />
                  </Grid>
                  <Grid xs={10} item>
                    <LoadingButton
                      type="submit"
                      variant="contained"
                      loading={loading}
                    >
                      {t('submit')}
                    </LoadingButton>
                  </Grid>
                </Grid>
                <Grid xs={6} item container>
                  <Grid item xs={12}>
                    <b>{t('availableBalance')}</b>:{' '}
                    {info?.bitcoinWalletBalance.availableBalance} sat
                  </Grid>
                  <Grid item xs={12}>
                    <b>{t('estimatedFee')}</b>:{' '}
                    {info?.bitcoinWalletBalance.currentReferenceFee} sat
                  </Grid>
                  <Grid xs={12} item>
                    <Alert
                      variant="standard"
                      severity="info"
                      className="mx-auto"
                    >
                      {t('feeInfo')}
                    </Alert>
                  </Grid>
                </Grid>
              </Grid>
            </Form>
          </Formik>
        )}
      </Panel.Body>
    </Panel.Container>
  )
}
