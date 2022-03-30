import React from 'react'
import {
  List,
  ListItem,
  ListItemText,
  Popover,
  Typography
} from '@mui/material'

interface Props {
  tokens: string[]
  id: string
  anchorEl: HTMLElement | null
  handleClose: () => void
}

export const TokenPopup: React.FC<Props> = ({
  tokens,
  id,
  anchorEl,
  handleClose
}) => {
  return (
    <Popover
      id={id}
      open={Boolean(anchorEl)}
      anchorEl={anchorEl}
      onClose={handleClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center'
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center'
      }}
    >
      <List>
        {tokens.map((token: string, key: number) => (
          <ListItem key={key}>
            <ListItemText>
              <Typography mr={5} sx={{ fontWeight: 'bold' }}>
                #{key}
              </Typography>
            </ListItemText>
            <ListItemText>{token}</ListItemText>
          </ListItem>
        ))}
      </List>
    </Popover>
  )
}
