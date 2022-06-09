import { setupServer, SetupServerApi } from 'msw/node'
import { rest } from 'msw'
import {
  currentPath,
  fireEvent,
  render,
  screen,
  waitFor
} from 'tests/test-utils'
import { routes } from 'webService/routes'
import { NewTransactionInfo } from 'webService/interface/transaction'
import { TransactionFormPage } from 'pages/userPanel/transactions/TransactionFormPage'

describe('TransactionFormPage', () => {
  let newInfoResponse: NewTransactionInfo
  let server: SetupServerApi

  const fillForm = async (amount: number, address: string) => {
    await waitFor(() => {
      fireEvent.change(screen.getByLabelText('Amount'), {
        target: { value: amount }
      })
      fireEvent.change(screen.getByLabelText('Address'), {
        target: { value: address }
      })
    })
    await waitFor(() => screen.getByRole('button', { name: 'Submit' }).click())
  }

  beforeEach(() => {
    newInfoResponse = {
      pendingExists: false,
      bitcoinWalletBalance: {
        availableBalance: 10000,
        unconfirmedBalance: 0,
        currentReferenceFee: 1000
      }
    }
    server = setupServer(
      rest.post(routes.transactions.index, (req, res, ctx) => {
        return res(ctx.status(201))
      }),
      rest.get(routes.transactions.newInfo, (req, res, ctx) => {
        return res(ctx.json(newInfoResponse))
      })
    )
    server.listen()
    render(<TransactionFormPage />, { location: '/panel/transactions/new' })
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('should render new transaction info data', async () => {
    await waitFor(() => {
      expect(
        screen.getByText('10000 sat', { exact: false })
      ).toBeInTheDocument()
      expect(screen.getByText('1000 sat', { exact: false })).toBeInTheDocument()
    })
  })

  it('should be redirected when there is pending transaction', async () => {
    newInfoResponse.pendingExists = true
    server.listen()
    render(<TransactionFormPage />, { location: '/panel/transactions/new' })
    await waitFor(() => {
      expect(currentPath()).toBe('/panel/transactions')
    })
  })

  it('should display error messages for invalid input', async () => {
    await fillForm(-4, 'dududu')
    await waitFor(() => {
      expect(
        screen.getByText('Amount must be larger that 1')
      ).toBeInTheDocument()
      expect(
        screen.getByText('Value is not a bitcoin address')
      ).toBeInTheDocument()
    })
    expect(currentPath()).toBe('/panel/transactions/new')
  })

  it('should display proper message when input is higher than balance', async () => {
    await fillForm(10001516, '2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv')
    await waitFor(() => {
      expect(
        screen.getByText('Value cannot exceed the available balance')
      ).toBeInTheDocument()
    })
    expect(currentPath()).toBe('/panel/transactions/new')
  })

  it('redirects to transactions list and shows success message', async () => {
    await fillForm(100, '2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv')
    await waitFor(() => {
      expect(
        screen.getByText('New transaction has been created')
      ).toBeInTheDocument()
    })
    expect(currentPath()).toBe('/panel/transactions')
  })
})
