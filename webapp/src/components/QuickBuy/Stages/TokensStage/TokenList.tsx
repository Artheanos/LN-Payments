import { List, ListItem } from '@mui/material'
import React from 'react'

import { TokenItem } from './TokenItem'

interface Props {
  setDisplayToken: (token: string) => void
  tokens: string[]
}

export const TokenList: React.FC<Props> = ({ tokens, setDisplayToken }) => {
  return (
    <List>
      {tokens.map((token: string, key: number) => {
        return (
          <ListItem key={key}>
            <TokenItem
              id={key}
              token={token}
              setDisplayToken={setDisplayToken}
            />
          </ListItem>
        )
      })}
    </List>
  )
}
