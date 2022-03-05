import React from 'react'
import { Outlet } from 'react-router-dom'

import { Navbar } from 'components/common/Navbar'

export const NavbarLayout: React.FC = () => {
  return (
    <>
      <Navbar />
      <Outlet />
    </>
  )
}
