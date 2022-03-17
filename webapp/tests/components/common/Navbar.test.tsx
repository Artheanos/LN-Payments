import React from 'react'

import { Navbar } from 'webapp/src/components/common/Navbar'
import { render, screen } from '../../test-utils'

describe('Navbar', () => {
  it('renders proper values', () => {
    render(<Navbar />)

    expect(screen.getByText('LN Payments')).toBeInTheDocument()
  })
})
