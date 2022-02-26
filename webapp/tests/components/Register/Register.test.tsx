import { render } from '../../test-utils'
import { Register } from '../../../src/components/Register/Register'

describe('Register form', () => {
  const { container } = render(<Register />)

  it('Should render registration form', () => {
    expect(container.querySelector(`input[name="email"]`)).toBeInTheDocument()
    expect(
      container.querySelector(`input[name="fullName"]`)
    ).toBeInTheDocument()
    expect(
      container.querySelector(`input[name="password"]`)
    ).toBeInTheDocument()
    expect(
      container.querySelector(`input[name="passwordConfirmation"]`)
    ).toBeInTheDocument()
    expect(container.querySelector(`button`)).toBeInTheDocument()
  })
})
