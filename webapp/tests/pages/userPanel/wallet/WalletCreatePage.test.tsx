import { rest } from 'msw'
import { setupServer } from 'msw/node'

import routesBuilder from 'routesBuilder'
import { fireEvent, render, screen, waitFor } from 'tests/test-utils'
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
      fireEvent.click(screen.getByText('Submit'))
    })
  }

  const submitFunction = jest.fn()

  beforeEach(async () => {
    server.listen()
    render(<WalletCreatePage onSubmit={submitFunction} />)
    await waitFor(() => {
      screen.getByText('Create wallet')
    })
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  describe('when params are valid', () => {
    beforeEach(
      async () => await fillForm(['admin@gmail.com', 'admin2@gmail.com'], '2')
    )

    it('should call submit function', async () => {
      await waitFor(() => {
        expect(submitFunction).toHaveBeenCalled()
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
    })
  })
})
