import { setupServer, SetupServerApi } from 'msw/node'
import { rest } from 'msw'

import { render, screen, waitFor } from 'tests/test-utils'
import { transactionMock } from 'tests/mockData/transactionMock'
import { routes } from 'webService/routes'
import { TransactionStatus } from 'webService/interface/transaction'
import { TransactionsPage } from 'pages/userPanel/transactions/TransactionsPage'

const transactionsPageMock = {
  pendingTransaction: transactionMock(TransactionStatus.PENDING),
  transactions: {
    empty: false,
    content: [
      transactionMock(TransactionStatus.FAILED),
      transactionMock(TransactionStatus.APPROVED)
    ],
    pageable: {
      pageSize: 2,
      pageNumber: 0
    },
    totalElements: 2
  }
}

const emptyResponse = { transactions: { empty: true } }

describe('TransactionsPage', () => {
  let server: SetupServerApi

  const init = (response: Record<string, unknown>) => {
    server = setupServer(
      rest.get(routes.transactions.index, (_, res, ctx) => {
        return res(ctx.json(response))
      })
    )
    server.listen()
    render(<TransactionsPage />)
  }

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('displays message when no transactions', async () => {
    init(emptyResponse)
    await waitFor(() => {
      expect(screen.getByText('No entries')).toBeInTheDocument()
    })
  })

  it('displays proper number of elements', async () => {
    init(transactionsPageMock)
    await waitFor(() => {
      expect(
        screen.queryAllByText('2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv').length
      ).toBe(3)
      expect(
        screen.queryAllByText('2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4').length
      ).toBe(3)
      expect(screen.queryAllByText('1100 sats').length).toBe(3)
      expect(
        screen.queryAllByText(
          new Date('2022-05-03T17:36:10.518877Z').toLocaleString()
        ).length
      ).toBe(3)
      expect(screen.getByText('PENDING')).toBeInTheDocument()
      expect(screen.getByText('FAILED')).toBeInTheDocument()
      expect(screen.getByText('APPROVED')).toBeInTheDocument()
    })
  })

  it('should display create button when no transactions pending', async () => {
    init(emptyResponse)
    await waitFor(() => {
      expect(screen.getByText('Create transaction')).toBeInTheDocument()
    })
  })

  it('should not display create button when transactions is pending', async () => {
    init(transactionsPageMock)
    await waitFor(() => {
      expect(screen.getByText('PENDING')).toBeInTheDocument()
      expect(screen.queryByText('Create transaction')).toBeNull()
    })
  })
})
