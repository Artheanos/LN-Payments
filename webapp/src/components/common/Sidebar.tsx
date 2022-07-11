import React from 'react'
import {
  AccessTime,
  AccountBalanceWalletOutlined,
  CurrencyExchange,
  MeetingRoomOutlined,
  PaidOutlined,
  ShoppingCartOutlined,
  SettingsOutlined,
  AccountCircleOutlined
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
          title="Wallet"
          icon={<AccountBalanceWalletOutlined />}
          route={routesBuilder.userPanel.wallet.index}
          adminOnly
        />
        <SidebarEntry
          title="Transactions"
          icon={<CurrencyExchange />}
          route={routesBuilder.userPanel.transactions.index}
          adminOnly
        />
        <SidebarEntry
          title="Admin Management"
          icon="AM"
          route={routesBuilder.userPanel.admins.index}
          adminOnly
        />
        <SidebarEntry
          title="Payments"
          icon={<PaidOutlined />}
          route={routesBuilder.userPanel.payments}
          adminOnly
        />
        <SidebarEntry
          title="Settings"
          icon={<SettingsOutlined />}
          route={routesBuilder.userPanel.serverSettings.index}
          adminOnly
        />
        <SidebarEntry
          title="Account"
          icon={<AccountCircleOutlined />}
          route={routesBuilder.userPanel.account.index}
        />
        <SidebarEntry
          title="Logout"
          icon={<MeetingRoomOutlined />}
          route={routesBuilder.logout}
        />
      </List>
    </Drawer>
  )
}
