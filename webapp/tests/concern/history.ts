import { BrowserHistory, createBrowserHistory } from 'history'

export const historyMock: {
  history: BrowserHistory
  initialLocation?: string
} = {
  history: createBrowserHistory()
}
