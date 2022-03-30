import { render, screen } from 'tests/test-utils'
import { History } from 'components/History/History'
import { setupServer, SetupServerApi } from 'msw/node'
import { rest } from 'msw'
import routesBuilder from 'routesBuilder'
import { waitFor } from '@testing-library/react'

describe('History', () => {
  let server: SetupServerApi

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  beforeAll(() => {
    render(<History />)
  })

  it('should display message when no payments', async () => {
    server = setupServer(
      rest.get(routesBuilder.api.payments.index, (req, res, ctx) => {
        return res(ctx.json({ empty: true }))
      })
    )
    server.listen()

    await waitFor(() => {
      expect(screen.getByText('No payments found!')).toBeInTheDocument()
    })
  })

  it('should display proper number of elements', () => {
    server = setupServer(
      rest.get(routesBuilder.api.payments.index, (req, res, ctx) => {
        return res(
          ctx.json({
            empty: false,
            content: [
              {
                paymentRequest: 'axa',
                timestamp: '2022-03-21 18:23:34.543684',
                expirationTimestamp: '2022-03-21 18:23:34.543684',
                price: 2138,
                numberOfTokens: 1,
                paymentStatus: 'CANCELLED',
                tokens: []
              }
            ]
          })
        )
      })
    )
    server.listen()

    waitFor(() => {
      expect(screen.getByText('axa')).toBeInTheDocument()
      expect(
        screen.getByText(
          new Date('2022-03-21 18:23:34.543684').toLocaleString()
        )
      ).toBeInTheDocument()
      expect(screen.getByText('2138')).toBeInTheDocument()
      expect(screen.getByText('CANCELLED')).toBeInTheDocument()
      expect(screen.getByText('none')).toBeInTheDocument()
      expect(screen.queryByText('Show')).not.toBeInTheDocument()
    })
  })
})
