import React, { ReactElement } from 'react'
import { Router } from 'react-router-dom'
import { I18nextProvider } from 'react-i18next'
import { render, RenderOptions } from '@testing-library/react'
import { Action, BrowserHistory, createBrowserHistory } from 'history'

import i18n from 'i18n'
import { NotificationProvider } from 'components/Context/NotificationContext'
import {
  defaultValue as defaultUserContextValue,
  UserContext
} from 'components/Context/UserContext'
import { Role } from 'webService/interface/user'

type CustomRenderProps = Partial<{
  location: string
  role: Role
  isLoggedIn: boolean
}>

type RenderWrapperProps = Omit<CustomRenderProps, 'location'>

const helpers: { history: BrowserHistory; initialLocation?: string } = {
  history: createBrowserHistory()
}

HTMLCanvasElement.prototype.getContext = () => null

const AllTheProviders: React.FC<RenderWrapperProps> = ({
  children,
  role = Role.USER,
  isLoggedIn = false
}) => {
  const user = { email: '', fullName: '', role: role }
  const token = '123'

  return (
    <Router
      navigator={helpers.history}
      navigationType={Action.Push}
      location={{ pathname: '/' }}
    >
      <UserContext.Provider
        value={{ ...defaultUserContextValue, user, token, isLoggedIn }}
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
  helpers.history = createBrowserHistory()
  helpers.history.push(options?.location || '/')
  return render(ui, {
    wrapper: ({ children }) => (
      <AllTheProviders {...options}>{children}</AllTheProviders>
    ),
    ...options
  })
}

const currentPath = () => helpers.history.location.pathname

export * from '@testing-library/react'
export { customRender as render, currentPath }
