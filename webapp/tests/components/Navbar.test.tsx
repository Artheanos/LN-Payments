import React from 'react'

import { BrowserRouter } from 'react-router-dom'
import { Navbar } from 'components/Navbar'
import { render, screen } from 'tests/test-utils'

describe('Navbar', () => {
  it('renders proper values', () => {
    render(
      <BrowserRouter>
        <Navbar />
      </BrowserRouter>
    )

    expect(screen.getByText('LN Payments')).toBeInTheDocument()
  })
})
