import { Box, LinearProgress, Tooltip } from '@mui/material'
import React from 'react'

type Props = {
  tooltipContent: string
  maxValue: number
  color: 'primary' | 'secondary'
  balance: number
}

export const BalanceProgressBar: React.FC<Props> = ({
  maxValue,
  color,
  tooltipContent,
  balance
}) => (
  <Tooltip title={tooltipContent}>
    <Box>
      <LinearProgress
        value={normalise(balance, maxValue)}
        variant="determinate"
        color={color}
        sx={{
          height: 16,
          borderRadius: 5
        }}
      />
      <Box className="flex justify-between text-slate-500">
        <span>0</span>
        <span>{maxValue.toLocaleString()}</span>
      </Box>
    </Box>
  </Tooltip>
)

const normalise = (value: number, maxValue: number) =>
  Math.min((value * 100) / maxValue, 100)
