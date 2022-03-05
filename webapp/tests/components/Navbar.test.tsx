import React from 'react'

import { Navbar } from 'components/common/Navbar'
import { render, screen } from 'tests/test-utils'

describe('Navbar', () => {
  it('renders proper values', () => {
    render(<Navbar />)

    expect(screen.getByText('LN Payments')).toBeInTheDocument()
  })
})
