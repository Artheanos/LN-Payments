import React, { ReactElement } from 'react'
import { Router } from 'react-router-dom'
import { I18nextProvider } from 'react-i18next'
import { render, RenderOptions } from '@testing-library/react'
import { Action, createBrowserHistory } from 'history'

import i18n from 'i18n'
import { NotificationProvider } from 'components/Context/NotificationContext'
import {
  defaultValue as defaultUserContextValue,
  UserContext
} from 'components/Context/UserContext'
import { Role } from 'webService/interface/user'

import { historyMock } from './concern/history'
import './concern/polyfills/array'
import './concern/polyfills/htmlCanvasElement'

type CustomRenderProps = Partial<{
  location: string
  role: Role
  isLoggedIn: boolean
}>

type RenderWrapperProps = Omit<CustomRenderProps, 'location'>

const AllTheProviders: React.FC<RenderWrapperProps> = ({
  children,
  role = Role.USER
}) => {
  const user = { email: '', fullName: '', role: role }
  const token = '123'
  const isLoggedIn = Boolean(user && token)
  const hasAccount = isLoggedIn && user?.role !== Role.TEMPORARY

  return (
    <Router
      navigator={historyMock.history}
      navigationType={Action.Push}
      location={{ pathname: '/' }}
    >
      <UserContext.Provider
        value={{
          ...defaultUserContextValue,
          user,
          token,
          isLoggedIn,
          hasAccount
        }}
      >
        <NotificationProvider>
          <I18nextProvider i18n={i18n}>{children}</I18nextProvider>
        </NotificationProvider>
      </UserContext.Provider>
    </Router>
  )
}

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'> & CustomRenderProps
) => {
  historyMock.history = createBrowserHistory()
  historyMock.history.push(options?.location || '/')
  return render(ui, {
    wrapper: ({ children }) => (
      <AllTheProviders {...options}>{children}</AllTheProviders>
    ),
    ...options
  })
}

const currentPath = () => historyMock.history.location.pathname

export * from '@testing-library/react'
export { customRender as render, currentPath }
