import React, { useContext } from 'react'
import { ListItem, ListItemIcon, ListItemText } from '@mui/material'
import { Link, useLocation } from 'react-router-dom'
import { UserContext } from 'components/Context/UserContext'

type Props = {
  title: string
  icon: React.ReactNode
  route: string
  adminOnly?: boolean
}

export const SidebarEntry: React.FC<Props> = ({
  icon,
  route,
  title,
  adminOnly
}: Props) => {
  const { user } = useContext(UserContext)
  const isSelected = useLocation().pathname.startsWith(route)

  if (adminOnly && user?.role !== 'ROLE_ADMIN') {
    return null
  }

  return (
    <Link to={route}>
      <ListItem selected={isSelected} button key={title}>
        <ListItemIcon>{icon}</ListItemIcon>
        <ListItemText primary={title} />
      </ListItem>
    </Link>
  )
}
