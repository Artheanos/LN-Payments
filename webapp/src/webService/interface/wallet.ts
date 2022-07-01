import { AdminUser } from './user'

export interface ChannelsBalance {
  totalBalance: number
  openedChannels: number
  autoChannelCloseLimit: number
}

export interface LightningWalletBalance {
  availableBalance: number
  unconfirmedBalance: number
  autoTransferLimit: number
}

export interface BitcoinWalletBalance {
  availableBalance: number
  unconfirmedBalance: number
  currentReferenceFee: number
}

export interface WalletInfo {
  address: string
  admins: AdminUser[]
  channelsBalance: ChannelsBalance
  lightningWalletBalance: LightningWalletBalance
  bitcoinWalletBalance: BitcoinWalletBalance
  totalIncomeData: ChartData[]
}

export interface WalletForm {
  adminEmails: string[]
  minSignatures: number
}

export interface ChartData {
  key: string
  value: number
}
