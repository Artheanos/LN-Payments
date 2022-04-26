import { render, screen } from 'tests/test-utils'
import React from 'react'
import { Sidebar } from 'components/common/Sidebar'

describe('Sidebar', () => {
  it('renders proper values', () => {
    render(<Sidebar />)

    expect(screen.getByText('Quick Buy')).toBeInTheDocument()
    expect(screen.getByText('History')).toBeInTheDocument()
  })
})
