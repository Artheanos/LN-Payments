import { rest } from 'msw'
import { setupServer } from 'msw/node'

import { currentPath, render, screen, waitFor } from 'tests/test-utils'
import { WalletPage } from 'pages/userPanel/wallet/WalletPage'

describe('WalletPage', () => {
  let responseStatus: number
  const server = setupServer(
    rest.get('/api/wallet', (_, res, ctx) => {
      return res(
        ctx.status(responseStatus),
        ctx.json({
          bitcoinWalletBalance: {
            availableBalance: 21,
            unconfirmedBalance: 37
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
          }
        })
      )
    })
  )

  const init = async (_responseStatus: number) => {
    responseStatus = _responseStatus
    server.listen()
    render(<WalletPage />, { location: '/panel/wallet' })
  }

  describe('when wallet exists', () => {
    beforeEach(() => init(200))

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
      })
    })
  })

  describe('when wallet does not exist', () => {
    beforeEach(() => init(404))

    it('redirects with a message', async () => {
      await waitFor(() => {
        expect(
          screen.queryByText('Redirected to wallet creation form')
        ).toBeInTheDocument()
        expect(currentPath()).toBe('/panel/wallet/new')
      })
    })
  })
})
