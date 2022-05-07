import { render, screen } from 'tests/test-utils'
import { ActionButton } from 'components/wallet/ActionButton'
import React from 'react'
import { waitFor } from '@testing-library/react'

describe('ActionButton', () => {
  let mockAction: () => void

  beforeEach(() => {
    mockAction = jest.fn()
  })

  it('should call function on click when no popup', () => {
    render(
      <ActionButton text="test button" action={mockAction} disabled={false} />
    )
    const button = screen.getByText('test button')
    button.click()
    expect(button).toBeInTheDocument()
    expect(mockAction).toHaveBeenCalled()
  })

  it('should not call function when button is disabled', () => {
    render(
      <ActionButton text="test button" action={mockAction} disabled={true} />
    )
    const button = screen.getByText('test button')
    button.click()
    expect(button).toBeInTheDocument()
    expect(button).toBeDisabled()
    expect(mockAction).not.toHaveBeenCalled()
  })

  it('should call action after popup click', async () => {
    render(
      <ActionButton
        text="test button"
        action={mockAction}
        modalMessage="modal message"
      />
    )
    const button = screen.getByText('test button')
    button.click()
    expect(mockAction).not.toHaveBeenCalled()
    await waitFor(() => {
      expect(screen.getByText('modal message')).toBeInTheDocument()
    })
    screen.getByText('Yes').click()
    expect(mockAction).toHaveBeenCalled()
  })
})
