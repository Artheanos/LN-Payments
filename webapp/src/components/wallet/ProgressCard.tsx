import React from 'react'
import { WalletCard } from './WalletCard'
import { BalanceProgressBar } from './BalanceProgressBar'
import { Typography } from '@mui/material'

type Props = {
  headerText: string
  value: number
  maxValue: number
  tooltipContent: string
  bottomText: string
  unit: string
  color: 'primary' | 'secondary'
}

export const ProgressCard: React.FC<Props> = ({
  bottomText,
  headerText,
  tooltipContent,
  value,
  maxValue,
  unit,
  color
}) => {
  return (
    <WalletCard standardSize={4}>
      <span className="text-xl font-bold">{headerText}</span>
      <Typography color={color}>
        <span className="text-3xl font-extrabold">
          {value.toLocaleString() + ' ' + unit}
        </span>
      </Typography>
      <BalanceProgressBar
        tooltipContent={tooltipContent}
        maxValue={maxValue}
        color={color}
        balance={value}
      />
      <span className="text-slate-500">{bottomText}</span>
    </WalletCard>
  )
}
