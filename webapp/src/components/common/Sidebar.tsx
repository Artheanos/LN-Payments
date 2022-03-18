import React from 'react'
import { Drawer, List, Toolbar } from '@mui/material'
import { SidebarEntry } from './SidebarEntry'
import { AccessTime, ShoppingCartOutlined } from '@mui/icons-material'
import routesBuilder from '../../routesBuilder'
import { useTranslation } from 'react-i18next'

const drawerWidth = '19rem'

export const Sidebar: React.FC = () => {
  const { t } = useTranslation('common')

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        ['& .MuiDrawer-paper']: { width: drawerWidth, boxSizing: 'border-box' }
      }}
    >
      <Toolbar />
      <List>
        <SidebarEntry
          title={t('sidebar.quickBuy')}
          icon={<ShoppingCartOutlined />}
          route={routesBuilder.quickBuy}
        />
        <SidebarEntry
          title={t('sidebar.history')}
          icon={<AccessTime />}
          route={routesBuilder.history}
        />
      </List>
    </Drawer>
  )
}
