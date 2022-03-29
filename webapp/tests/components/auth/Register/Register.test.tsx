import { render, screen } from 'tests/test-utils'
import { Register } from 'components/auth/Register/Register'
import { setupServer } from 'msw/node'
import { rest } from 'msw'
import routesBuilder from 'routesBuilder'
import { fireEvent, waitFor } from '@testing-library/react'

describe('Register form', () => {
  let statusCode = 200
  const server = setupServer(
    rest.post(routesBuilder.api.auth.register, (req, res, ctx) => {
      return res(ctx.status(statusCode))
    })
  )
  const fillForm = () => {
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'test@test.pl' }
    })
    fireEvent.change(screen.getByLabelText('Full name'), {
      target: { value: 'testest' }
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'zaq1@WSX' }
    })
    fireEvent.change(screen.getByLabelText('Repeat your password'), {
      target: { value: 'zaq1@WSX' }
    })
  }

  afterEach(() => server.resetHandlers())
  afterAll(() => server.close())

  beforeEach(() => {
    render(<Register />)
  })

  it('Should render registration form', () => {
    expect(screen.getByLabelText('Email')).toBeInTheDocument()
    expect(screen.getByLabelText('Full name')).toBeInTheDocument()
    expect(screen.getByLabelText('Password')).toBeInTheDocument()
    expect(screen.getByLabelText('Repeat your password')).toBeInTheDocument()
    expect(screen.getAllByRole('button')[0]).toBeInTheDocument()
  })

  it('should register user with proper data', async () => {
    statusCode = 201
    server.listen()
    fillForm()
    screen.getAllByRole('button')[0].click()
    await waitFor(() => {
      expect(
        screen.getByText(
          'You have been successfully registered. You can now log in!'
        )
      ).toBeInTheDocument()
    })
  })

  it('should display error when 4xx returned', async () => {
    statusCode = 400
    server.listen()
    fillForm()
    screen.getAllByRole('button')[0].click()
    await waitFor(() => {
      expect(screen.getByText('Bad data provided')).toBeInTheDocument()
    })
  })
})
