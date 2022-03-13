import React from 'react'
import { Drawer, List, Toolbar } from '@mui/material'
import { SidebarEntry } from './SidebarEntry'
import { AccessTime, ShoppingCartOutlined } from '@mui/icons-material'
import routesBuilder from '../../routesBuilder'

const drawerWidth = 300

export const Sidebar: React.FC = () => {
  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' }
      }}
    >
      <Toolbar />
      <List>
        <SidebarEntry
          title="Quick Buy"
          icon={<ShoppingCartOutlined />}
          route={routesBuilder.quickBuy}
        />
        <SidebarEntry
          title="History"
          icon={<AccessTime />}
          route={routesBuilder.history}
        />
      </List>
    </Drawer>
  )
}
