import { setupServer } from 'msw/node'
import { rest } from 'msw'
import { fireEvent, render, screen, waitFor } from 'tests/test-utils'
import React from 'react'
import { WalletLayout } from 'components/Layouts/WalletLayout'
import routesBuilder from 'routesBuilder'
import userEvent from '@testing-library/user-event'
import { cleanup } from '@testing-library/react'

const adminsMock = [
  {
    email: 'jan@gmail.com',
    hasKey: false
  },
  {
    email: 'admin@gmail.com',
    hasKey: true
  },
  {
    email: 'admin2@gmail.com',
    hasKey: true
  }
]

describe('WalletLayout tests', () => {
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
          address: 'address'
        })
      )
    }),
    rest.get(routesBuilder.api.admins.index, (req, res, ctx) => {
      return res(ctx.json({ content: adminsMock }))
    }),
    rest.post(routesBuilder.api.wallet.index, (req, res, ctx) => {
      return res(ctx.status(201))
    })
  )

  beforeAll(() => {
    window.ResizeObserver = jest.fn().mockImplementation(() => ({
      observe: jest.fn(),
      unobserve: jest.fn(),
      disconnect: jest.fn()
    }))
  })

  const init = async (_responseStatus: number) => {
    responseStatus = _responseStatus
    server.listen()
    render(<WalletLayout />)
  }

  afterEach(() => {
    jest.resetModules()
    jest.clearAllMocks()
    server.resetHandlers()
    cleanup()
  })
  afterAll(() => {
    server.close()
  })

  describe('when wallet exists', () => {
    beforeEach(() => init(200))

    it('should render wallet page', async () => {
      await waitFor(() => {
        expect(screen.queryByText('Wallet')).toBeInTheDocument()
        expect(screen.queryByText('address')).toBeInTheDocument()
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
        expect(screen.queryByText('Create wallet')).toBeInTheDocument()
      })
    })

    it('should redirect after form is submitted successfully', async () => {
      server.resetHandlers()
      await init(200)
      await waitFor(() => {
        screen.getByText('Create wallet')
      })
      await waitFor(async () => {
        fireEvent.change(
          screen.getByLabelText('Minimum number of signatures'),
          {
            target: { value: 1 }
          }
        )
        await userEvent.click(screen.getByLabelText('Admins'))
        for (const email of ['admin@gmail.com']) {
          await userEvent.click(screen.getByText(email))
        }
        await userEvent.click(screen.getAllByLabelText('Admins')[0])
        fireEvent.click(screen.getByText('Submit'))
      })
      await waitFor(() => {
        expect(screen.queryByText('Wallet')).toBeInTheDocument()
        expect(screen.queryByText('address')).toBeInTheDocument()
      })
    })
  })
})
