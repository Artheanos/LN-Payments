import React, { useEffect, useState } from 'react'
import { Button, CircularProgress, Grid } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { Client as StompClient } from '@stomp/stompjs'

import routesBuilder from 'routesBuilder'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'
import { QRInfo } from './QRInfo'
import { StageProps } from 'components/QuickBuy/StageProps'
import { WsTransactionResponse } from 'webService/interface/transaction'
import { refreshTokenFactory } from 'webService/requests'
import { millisecondsToClock, useCountdown } from 'utils/time'

const websocketBuilder = (onConnect: () => void) => {
  return new StompClient({
    brokerURL: routesBuilder.api.payments.ws(),
    connectHeaders: {
      Authorization: `Bearer ${refreshTokenFactory()}`
    },
    onConnect,
    onStompError: console.error,
    debug: console.debug,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000
  })
}

export const TransactionStage: React.FC<StageProps> = ({
  onPrevious,
  onNext,
  setStageIndex,
  payment,
  paymentInfo,
  setPayment,
  setTokens
}) => {
  const { t } = useTranslation('quickBuy')
  const [modalVisible, setModalVisible] = useState(false)
  const [socket, setSocket] = useState<StompClient>()

  const timeLeft = useCountdown(payment?.expirationTimestamp, () => {
    setModalVisible(true)
    setPayment(undefined)
  })

  const cancelTransaction = () => {
    setPayment(undefined)
    onPrevious()
  }

  useEffect(() => {
    if (!payment) return

    const confirmTransaction = (tokens: string[]) => {
      setTokens(tokens)
      setPayment(undefined)
      onNext()
    }

    const websocket = websocketBuilder(() => {
      setSocket(websocket)
      websocket.subscribe(`/topic/${payment.paymentTopic}`, (message) => {
        const response: WsTransactionResponse = JSON.parse(message.body)
        confirmTransaction(response.tokens)
      })
    })
    websocket.activate()
    return () => {
      websocket.deactivate()
    }
  }, [onNext, payment, setPayment, setTokens])

  return (
    <div>
      <ConfirmationModal
        confirmButtonContent={t('transaction.modal.button')}
        message={t('transaction.modal.timeoutMessage')}
        onConfirm={() => setStageIndex?.(0)}
        open={modalVisible}
        setOpen={setModalVisible}
      />
      {socket && payment && paymentInfo ? (
        <div className="mx-auto w-96">
          <Grid container>
            <Grid xs={12} item>
              <QRInfo details={payment} info={paymentInfo} />
            </Grid>
            <Grid xs={6} item>
              <Button variant="contained" onClick={cancelTransaction}>
                {t('common:cancel')}
              </Button>
            </Grid>
            <Grid xs={6} item>
              <Button color="warning" disabled variant="contained">
                {millisecondsToClock(timeLeft)}
              </Button>
            </Grid>
          </Grid>
        </div>
      ) : (
        <CircularProgress />
      )}
    </div>
  )
}
