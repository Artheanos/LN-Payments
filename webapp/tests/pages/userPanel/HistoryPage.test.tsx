import routesBuilder from 'routesBuilder'
import { rest } from 'msw'
import { setupServer, SetupServerApi } from 'msw/node'

import { HistoryPage } from 'pages/userPanel/HistoryPage'
import { paymentMock } from 'tests/mockData/paymentMock'
import { render, screen, waitFor } from 'tests/test-utils'

const paymentsMock = {
  empty: false,
  content: [{ ...paymentMock, price: 3000 }],
  pageable: {
    pageSize: 2,
    pageNumber: 0
  },
  totalElements: 1
}

const emptyResponse = { empty: true }

describe('HistoryPage', () => {
  let server: SetupServerApi

  const init = (response: Record<string, unknown>) => {
    server = setupServer(
      rest.get(routesBuilder.api.payments.index, (_, res, ctx) => {
        return res(ctx.json(response))
      })
    )
    server.listen()
    render(<HistoryPage />)
  }

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('displays message when no payments', async () => {
    init(emptyResponse)
    await waitFor(() => {
      expect(screen.getByText('No entries')).toBeInTheDocument()
    })
  })

  it('displays proper number of elements', async () => {
    init(paymentsMock)
    await waitFor(() => {
      expect(screen.getByText('axa')).toBeInTheDocument()
      expect(
        screen.getByText(
          new Date('2022-03-21 18:23:34.543684').toLocaleString()
        )
      ).toBeInTheDocument()
      expect(screen.getByText('3000')).toBeInTheDocument()
      expect(screen.getByText('CANCELLED')).toBeInTheDocument()
      expect(screen.getByText('none')).toBeInTheDocument()
      expect(screen.queryByText('Show')).not.toBeInTheDocument()
      expect(screen.queryAllByText('axa').length).toBe(1)
    })
  })
})
