import { setupServer } from 'msw/node'
import { rest } from 'msw'
import routesBuilder from 'routesBuilder'
import { waitFor } from '@testing-library/react'
import { render, screen } from 'tests/test-utils'
import { ActionsCard } from 'components/wallet/ActionsCard'

describe('ActionsCard', () => {
  let statusCode = 200
  const server = setupServer(
    rest.post(routesBuilder.api.wallet.closeChannels, (req, res, ctx) => {
      return res(ctx.status(statusCode))
    }),
    rest.post(routesBuilder.api.wallet.transfer, (req, res, ctx) => {
      return res(ctx.status(statusCode))
    })
  )

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  beforeEach(() => {
    statusCode = 200
    render(<ActionsCard channelsBalance={1} lightningWalletBalance={1} />)
  })

  it('should call closeChannels on closeChannels click and success', async () => {
    server.listen()
    screen.getByText('Close Channels').click()
    await waitFor(() => {
      expect(screen.getByText('Closing channels...')).toBeInTheDocument()
    })
  })

  it('should call closeChannels on closeChannels click and fail', async () => {
    statusCode = 400
    server.listen()
    screen.getByText('Close Channels').click()
    await waitFor(() => {
      expect(
        screen.getByText('Error while closing channels.')
      ).toBeInTheDocument()
    })
  })

  it('should call transfer on click and success', async () => {
    server.listen()
    screen.getByText('Transfer Between Wallets').click()
    await waitFor(() => {
      expect(
        screen.getByText('Transferring funds to BTC wallet...')
      ).toBeInTheDocument()
    })
  })

  it('should call closeChannels on closeChannels click and fail', async () => {
    statusCode = 400
    server.listen()
    screen.getByText('Transfer Between Wallets').click()
    await waitFor(() => {
      expect(
        screen.getByText('Error while transferring funds.')
      ).toBeInTheDocument()
    })
  })

  it('should call closeChannels with force on closeChannels click and success', async () => {
    server.listen()
    screen.getByText('Force Close Channels').click()
    await waitFor(() => {
      screen.getByText('Yes').click()
    })
    await waitFor(() => {
      expect(screen.getByText('Closing channels...')).toBeInTheDocument()
    })
  })

  it('should call closeChannels with force on closeChannels click and fail', async () => {
    statusCode = 400
    server.listen()
    screen.getByText('Force Close Channels').click()
    await waitFor(() => {
      screen.getByText('Yes').click()
    })
    await waitFor(() => {
      expect(
        screen.getByText('Error while closing channels.')
      ).toBeInTheDocument()
    })
  })
})
