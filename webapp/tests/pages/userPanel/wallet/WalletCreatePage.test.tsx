import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import {
  currentPath,
  fireEvent,
  render,
  screen,
  waitFor
} from 'tests/test-utils'
import userEvent from '@testing-library/user-event'
import { WalletCreatePage } from 'pages/userPanel/wallet/WalletCreatePage'

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

describe('WalletCreatePage', () => {
  const server = setupServer(
    rest.post(routesBuilder.api.wallet.index, (req, res, ctx) => {
      return res(ctx.status(201))
    }),
    rest.get(routesBuilder.api.admins.index, (req, res, ctx) => {
      return res(ctx.json({ content: adminsMock }))
    })
  )

  const fillForm = async (adminEmails: string[], minSignatures: string) => {
    await waitFor(async () => {
      fireEvent.change(screen.getByLabelText('Minimum number of signatures'), {
        target: { value: minSignatures }
      })
      await userEvent.click(screen.getByLabelText('Admins'))
      for (const email of adminEmails) {
        await userEvent.click(screen.getByText(email))
      }
      await userEvent.click(screen.getAllByLabelText('Admins')[0])
      fireEvent.click(await screen.getByText('Submit'))
    })
  }

  beforeEach(() => {
    server.listen()
    render(<WalletCreatePage />, { location: '/panel/wallet/new' })
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  describe('when params are valid', () => {
    beforeEach(
      async () => await fillForm(['admin@gmail.com', 'admin2@gmail.com'], '2')
    )

    it('redirects to wallet page', async () => {
      await waitFor(() => {
        expect(currentPath()).toBe('/panel/wallet')
      })
    })
  })

  describe('when entering number of signatures greater than the number of admins', () => {
    beforeEach(async () => await fillForm(['admin@gmail.com'], '2'))

    it('stays on the page and shows error message', async () => {
      await waitFor(() => {
        expect(
          screen.getByText(
            'Number of signatures must not be greater than the amount of admins'
          )
        ).toBeInTheDocument()
      })
      expect(currentPath()).toBe('/panel/wallet/new')
    })
  })
})
