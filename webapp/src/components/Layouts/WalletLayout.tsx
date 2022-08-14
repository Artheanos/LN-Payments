import React, { useEffect, useState } from 'react'
import { useNotification } from 'components/Context/NotificationContext'
import { WalletInfo } from 'webService/interface/wallet'
import { api } from 'webService/requests'
import { WalletLoadingSkeleton } from 'components/wallet/WalletLoadingSkeleton'
import { WalletPage } from 'pages/userPanel/wallet/WalletPage'
import { WalletCreatePage } from 'pages/userPanel/wallet/WalletCreatePage'
import { FormikValues } from 'formik'
import { WalletForm } from 'components/wallet/create/form'
import { useTranslation } from 'react-i18next'

export const WalletLayout: React.FC = () => {
  const { t } = useTranslation('wallet')
  const notify = useNotification()
  const [loading, setLoading] = useState(true)
  const [isWalletCreated, setIsWalletCreated] = useState(true)
  const [walletInfo, setWalletInfo] = useState<WalletInfo>()

  useEffect(() => {
    if (!isWalletCreated) return
    let isMounted = true

    api.wallet.getInfo().then(({ status, data }) => {
      if (!isMounted) return

      if (status === 404) {
        notify(t('createForm.redirection'), 'info')
        setIsWalletCreated(false)
        setLoading(false)
        return
      }
      if (status === 200) {
        setWalletInfo(data)
        setIsWalletCreated(true)
        setLoading(false)
        return
      }
    })
    return () => {
      isMounted = false
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isWalletCreated])

  const onSubmit = async (values: FormikValues) => {
    const { status } = await api.wallet.create(values as WalletForm)

    if (status === 201) {
      setLoading(true)
      setIsWalletCreated(true)
    } else {
      alert(t('common:error.generic'))
    }
  }

  if (loading) return <WalletLoadingSkeleton />

  if (!isWalletCreated) return <WalletCreatePage onSubmit={onSubmit} />

  return <WalletPage walletInfo={walletInfo} />
}
