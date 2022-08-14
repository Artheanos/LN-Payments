import { render, screen, waitFor } from 'tests/test-utils'
import { WalletPage } from 'pages/userPanel/wallet/WalletPage'
import React from 'react'
import { WalletInfo } from 'webService/interface/wallet'

describe('WalletPage', () => {
  const testData: WalletInfo = {
    bitcoinWalletBalance: {
      availableBalance: 21,
      unconfirmedBalance: 37,
      currentReferenceFee: 1
    },
    channelsBalance: {
      totalBalance: 123,
      openedChannels: 8,
      autoChannelCloseLimit: 789
    },
    lightningWalletBalance: {
      availableBalance: 147,
      autoTransferLimit: 369,
      unconfirmedBalance: 40
    },
    totalIncomeData: [
      {
        key: '2022-04',
        value: 3
      },
      {
        key: '2022-05',
        value: 2
      },
      {
        key: '2022-06',
        value: 1
      }
    ],
    admins: [],
    address: 'ddd'
  }

  beforeAll(() => {
    window.ResizeObserver = jest.fn().mockImplementation(() => ({
      observe: jest.fn(),
      unobserve: jest.fn(),
      disconnect: jest.fn()
    }))
  })

  beforeEach(() => {
    render(<WalletPage walletInfo={testData} />)
  })

  describe('when wallet exists', () => {
    it('displays wallet info', async () => {
      await waitFor(() => {
        expect(screen.getByText('Bitcoin Wallet')).toBeInTheDocument()
        expect(screen.getByText('21 sats')).toBeInTheDocument()
        expect(screen.getByText('37 sats unconfirmed')).toBeInTheDocument()
        expect(screen.getByText('Channels Balance')).toBeInTheDocument()
        expect(screen.getByText('8 open channels')).toBeInTheDocument()
        expect(screen.getByText('789')).toBeInTheDocument()
        expect(screen.getByText('Lightning Wallet')).toBeInTheDocument()
        expect(screen.getByText('147 sats')).toBeInTheDocument()
        expect(screen.getByText('40 sats unconfirmed')).toBeInTheDocument()
        expect(screen.getByText('369')).toBeInTheDocument()
        expect(screen.getByText('Total income')).toBeInTheDocument()
        expect(screen.getByText('ddd')).toBeInTheDocument()
      })
    })
  })
})
