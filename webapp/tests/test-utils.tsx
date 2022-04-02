import React, { FC, ReactElement } from 'react'
import { Router } from 'react-router-dom'
import { I18nextProvider } from 'react-i18next'
import { render, RenderOptions } from '@testing-library/react'
import { Action, BrowserHistory, createBrowserHistory } from 'history'

import i18n from 'i18n'
import { NotificationProvider } from 'components/Context/NotificationContext'

const helpers: { history: BrowserHistory; initialLocation?: string } = {
  history: createBrowserHistory()
}

HTMLCanvasElement.prototype.getContext = () => null

const AllTheProviders: FC = (props) => (
  <Router
    navigator={helpers.history}
    navigationType={Action.Push}
    location={{ pathname: '/' }}
  >
    <NotificationProvider>
      <I18nextProvider i18n={i18n}>{props.children}</I18nextProvider>
    </NotificationProvider>
  </Router>
)

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'> & { location: string }
) => {
  helpers.history = createBrowserHistory()
  // if (options?.location) {
  helpers.history.push(options?.location || '/')
  // }
  return render(ui, { wrapper: AllTheProviders, ...options })
}

const currentPath = () => helpers.history.location.pathname

export * from '@testing-library/react'
export { customRender as render, currentPath }
