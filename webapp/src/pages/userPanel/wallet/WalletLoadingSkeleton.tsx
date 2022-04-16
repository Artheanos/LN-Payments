import React from 'react'
import { Grid, Skeleton } from '@mui/material'

export const WalletLoadingSkeleton: React.FC = () => (
  <Grid container gap={3}>
    <Grid xs={6} item>
      <Skeleton variant="rectangular" height="16rem" />
    </Grid>
    <Grid xs={3} item>
      <Skeleton variant="rectangular" height="12rem" />
    </Grid>
    <Grid xs={12} item>
      <Skeleton variant="rectangular" height="12rem" />
    </Grid>
  </Grid>
)
