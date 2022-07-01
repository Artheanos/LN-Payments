import React from 'react'
import { WalletCard } from 'components/wallet/WalletCard'
import { ChartData } from 'webService/interface/wallet'

import { useTheme } from '@mui/material'
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts'
import { useTranslation } from 'react-i18next'

type Props = {
  chartData: ChartData[]
}

export const TotalIncomeChart: React.FC<Props> = ({ chartData }) => {
  const { t } = useTranslation('wallet')
  const theme = useTheme()

  return (
    <WalletCard standardSize={9}>
      <span className="text-xl font-bold">{t('chart.header')}</span>
      {chartData.length < 2 ? (
        <p className="pb-10 italic text-gray-500">{t('chart.noData')}</p>
      ) : (
        <ResponsiveContainer width="100%" height={185}>
          <AreaChart data={chartData}>
            <XAxis dataKey="key" />
            <YAxis />
            <Tooltip />
            <CartesianGrid strokeDasharray="8 5" />
            <Area
              type="monotone"
              dataKey="value"
              stroke={theme.palette.secondary.dark}
              fill={theme.palette.secondary.light}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </WalletCard>
  )
}
