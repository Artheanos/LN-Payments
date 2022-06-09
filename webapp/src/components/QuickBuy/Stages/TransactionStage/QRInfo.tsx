import React, { useState } from 'react'
import { Button, Grid } from '@mui/material'

import { useTranslation } from 'react-i18next'
import { QRComponent } from './QRComponent'
import { PaymentDetails, PaymentInfo } from 'webService/interface/payment'

interface PanelProps {
  details: PaymentDetails
  info: PaymentInfo
}

export const QRInfo: React.FC<PanelProps> = ({ details, info }) => {
  const { t } = useTranslation('quickBuy')
  const [showInvoiceInfo, setShowInvoiceInfo] = useState(true)
  const switchPanel = () => setShowInvoiceInfo((prevState) => !prevState)

  return (
    <div className="p-10 m-10 bg-purple-200 rounded-3xl">
      <Grid container className="gap-8">
        {showInvoiceInfo ? (
          <QRComponent
            value={details.paymentRequest}
            onCopyMessage={t('transaction.invoicePanel.onCopyMessage')}
          >
            {t('transaction.invoicePanel.header')}
            <p className="text-2xl font-bold">
              <span className="font-extrabold text-purple-700">
                {info.price}{' '}
              </span>
              {t('transaction.invoicePanel.currency')}
            </p>
          </QRComponent>
        ) : (
          <QRComponent
            value={info.nodeUrl}
            onCopyMessage={t('transaction.nodePanel.onCopyMessage')}
          >
            <p className="text-2xl font-bold">
              {t('transaction.nodePanel.header')}
            </p>
          </QRComponent>
        )}
        <Grid xs={12} item>
          <Button
            onClick={switchPanel}
            disabled={showInvoiceInfo}
            variant="contained"
            className="w-24 !rounded-r-none !rounded-l-2xl"
          >
            {t('invoice')}
          </Button>
          <Button
            onClick={switchPanel}
            disabled={!showInvoiceInfo}
            variant="contained"
            className="w-24 !rounded-r-2xl !rounded-l-none"
          >
            {t('node')}
          </Button>
        </Grid>
      </Grid>
    </div>
  )
}
