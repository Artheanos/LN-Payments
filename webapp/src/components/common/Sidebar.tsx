import React from 'react'
import {
  AccessTime,
  MeetingRoomOutlined,
  ShoppingCartOutlined
} from '@mui/icons-material'
import { Drawer, List, Toolbar } from '@mui/material'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'
import { SidebarEntry } from './SidebarEntry'

const drawerWidth = '17rem'

export const Sidebar: React.FC = () => {
  const { t } = useTranslation('common')

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        ['& .MuiDrawer-paper']: { width: drawerWidth, boxSizing: 'border-box' }
      }}
    >
      <Toolbar />
      <List>
        <SidebarEntry
          title={t('sidebar.quickBuy')}
          icon={<ShoppingCartOutlined />}
          route={routesBuilder.userPanel.quickBuy}
        />
        <SidebarEntry
          title={t('sidebar.history')}
          icon={<AccessTime />}
          route={routesBuilder.userPanel.history}
        />
        <SidebarEntry
          title="Logout"
          icon={<MeetingRoomOutlined />}
          route={routesBuilder.logout}
        />
        <SidebarEntry
          title="Admin Management"
          icon="AM"
          route={routesBuilder.adminPanel.admins.index}
          adminOnly
        />
      </List>
    </Drawer>
  )
}
