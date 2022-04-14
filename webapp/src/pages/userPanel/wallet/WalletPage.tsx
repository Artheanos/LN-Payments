import React, { useEffect, useState } from 'react'
import { Grid, Paper } from '@mui/material'
import { useNavigate } from 'react-router-dom'

import { api } from 'api'
import { useNotification } from 'components/Context/NotificationContext'
import { WalletLoadingSkeleton } from './WalletLoadingSkeleton'
import routesBuilder from 'routesBuilder'

export const WalletPage: React.FC = () => {
  const navigate = useNavigate()
  const notify = useNotification()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.wallet.getInfo().then(({ status }) => {
      if (status === 400) {
        notify('Redirected to wallet creation form', 'info')
        navigate(routesBuilder.userPanel.wallet.new)
      }
      if (status === 200) {
        setLoading(false)
      }
    })
  }, [navigate, notify])

  if (loading) return <WalletLoadingSkeleton />

  return (
    <Grid className="text-center" container gap={3}>
      <Grid xs="auto" item component={Paper}>
        <img
          src="https://peltiertech.com/images/2010-08/LineChart01.png"
          alt="chart"
        />
      </Grid>
      <Grid
        className="flex !flex-col justify-around p-5"
        xs={2}
        component={Paper}
        item
      >
        <span className="font-extrabold ">3 / 10</span>
        <hr />
        <span className="text-purple-700">LN Wallet</span>
      </Grid>
    </Grid>
  )
}
