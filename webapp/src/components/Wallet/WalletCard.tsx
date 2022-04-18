import React from 'react'
import { Grid, Paper } from '@mui/material'

type Props = {
  standardSize: number
}

export const WalletCard: React.FC<Props> = ({ standardSize, children }) => {
  return (
    <Grid md={standardSize} xs={12} item>
      <Paper className="flex !flex-col justify-around py-12 px-5 space-y-12">
        {children}
      </Paper>
    </Grid>
  )
}
