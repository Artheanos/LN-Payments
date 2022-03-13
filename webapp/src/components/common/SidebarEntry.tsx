import React from 'react'
import {
  ListItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material'
import { Link } from 'react-router-dom'

type Props = {
  title: string
  icon: React.ReactNode
  route: string
}

export const SidebarEntry: React.FC<Props> = (props: Props) => {
  return (
    <Link to={props.route}>
      <ListItem button key={props.title}>
        <ListItemIcon>{props.icon}</ListItemIcon>
        <ListItemText primary={props.title} />
      </ListItem>
    </Link>
  )
}
