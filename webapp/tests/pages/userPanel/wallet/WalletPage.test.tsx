import { rest } from 'msw'
import { setupServer } from 'msw/node'

import { currentPath, render, screen, waitFor } from 'tests/test-utils'
import { WalletPage } from 'pages/userPanel/wallet/WalletPage'

describe('WalletPage', () => {
  let responseStatus: number
  const server = setupServer(
    rest.get('/api/wallet', (_, res, ctx) => {
      return res(ctx.status(responseStatus))
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
        expect(screen.getByText('3 / 10')).toBeInTheDocument()
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
