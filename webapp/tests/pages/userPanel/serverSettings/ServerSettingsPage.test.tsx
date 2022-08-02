import { fireEvent, screen, render, waitFor } from 'tests/test-utils'
import { rest } from 'msw'
import { setupServer } from 'msw/node'

import { ServerSettingsPage } from 'pages/userPanel/serverSettings/ServerSettingsPage'
import { Settings } from 'webService/interface/settings'
import { routes } from 'webService/routes'

describe('ServerSettingsPage', () => {
  let updateStatus = 200

  const server = setupServer(
    rest.put(routes.settings.index, (req, res, ctx) => {
      return res(ctx.status(updateStatus))
    }),
    rest.get(routes.settings.index, (req, res, ctx) => {
      return res(ctx.json(validInputValues))
    })
  )

  const validInputValues: Omit<Settings, 'lastModification'> = {
    autoChannelCloseLimit: 100,
    autoTransferLimit: 100,
    description: 'Red payment',
    invoiceMemo: 'Token payment',
    paymentExpiryInSeconds: 300,
    price: 10,
    serverIpAddress: '',
    tokenDeliveryUrl: ''
  }

  let inputValues: Omit<Settings, 'lastModification'>

  beforeEach(async () => {
    server.listen()
    render(<ServerSettingsPage />)
    await screen.findByText('Server settings')
  })

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  it('fills the form with the current settings', async () => {
    await waitFor(() => {
      expect(screen.getByLabelText('Channel close limit')).toHaveValue(100)
      expect(screen.getByLabelText('Auto transfer limit')).toHaveValue(100)
      expect(screen.getByLabelText('Description')).toHaveValue('Red payment')
      expect(screen.getByLabelText('Invoice memo')).toHaveValue('Token payment')
      expect(screen.getByLabelText('Payment expiry')).toHaveValue(300)
      expect(screen.getByLabelText('Price')).toHaveValue(10)
    })
  })

  describe('updating settings', () => {
    beforeEach(async () => {
      await waitFor(() => {
        fireEvent.change(screen.getByLabelText('Channel close limit'), {
          target: { value: inputValues.autoChannelCloseLimit }
        })
        fireEvent.change(screen.getByLabelText('Auto transfer limit'), {
          target: { value: inputValues.autoTransferLimit }
        })
        fireEvent.change(screen.getByLabelText('Description'), {
          target: { value: inputValues.description }
        })
        fireEvent.change(screen.getByLabelText('Invoice memo'), {
          target: { value: inputValues.invoiceMemo }
        })
        fireEvent.change(screen.getByLabelText('Payment expiry'), {
          target: { value: inputValues.paymentExpiryInSeconds }
        })
        fireEvent.change(screen.getByLabelText('Price'), {
          target: { value: inputValues.price }
        })
        fireEvent.click(screen.getByText('Submit'))
      })
    })

    describe('when the form is valid', () => {
      beforeAll(() => {
        inputValues = { ...validInputValues }
      })

      describe('when api returns 200 status', () => {
        beforeAll(() => {
          updateStatus = 200
        })

        it('shows success message', async () => {
          await waitFor(() => {
            expect(
              screen.queryByText('Settings have been saved')
            ).toBeInTheDocument()
          })
        })
      })

      describe('when api returns error', () => {
        beforeAll(() => {
          updateStatus = 400
        })

        it('shows error message', async () => {
          await waitFor(() => {
            expect(
              screen.queryByText('Something went wrong')
            ).toBeInTheDocument()
          })
        })
      })
    })

    // describe('when the form contains an invalid entry', () => {
    //   beforeAll(() => {
    //     inputValues = { ...validInputValues, price: 0 }
    //   })
    //
    //   it('shows an error', async () => {
    //     await waitFor(() => {
    //       expect(
    //         screen.getByText('Price must be greater than or equal to 1')
    //       ).toBeInTheDocument()
    //     })
    //   })
    // })
  })
})
