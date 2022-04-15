import { rest } from 'msw'
import { setupServer, SetupServerApi } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { PaymentsPage } from 'pages/userPanel/PaymentsPage'
import { paymentMock } from 'tests/mockData/paymentMock'
import { render, screen, waitFor } from 'tests/test-utils'

const paymentsMock = {
  empty: false,
  content: [
    { ...paymentMock, price: 1000 },
    { ...paymentMock, price: 2000 }
  ],
  pageable: {
    pageSize: 2,
    pageNumber: 0
  },
  totalElements: 2
}

const emptyResponse = { empty: true }

describe('PaymentsPage', () => {
  let server: SetupServerApi
  const init = (response: Record<string, unknown>) => {
    server = setupServer(
      rest.get(routesBuilder.api.payments.all, (_, res, ctx) => {
        return res(ctx.json(response))
      })
    )
    server.listen()
    render(<PaymentsPage />)
  }

  it('displays no data message when response is empty', async () => {
    init(emptyResponse)

    await waitFor(() => {
      expect(screen.getByText('No entries')).toBeInTheDocument()
      expect(screen.queryAllByText('axa').length).toBe(0)
    })
  })

  it('displays proper number of elements', async () => {
    init(paymentsMock)

    await waitFor(() => {
      expect(screen.getByText('1000')).toBeInTheDocument()
      expect(screen.getByText('2000')).toBeInTheDocument()
      expect(screen.getAllByText('axa@email.com').length).toBe(2)
    })
  })
})
