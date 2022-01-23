import { render } from 'react-dom'
import { StrictMode } from 'react'

import './i18n'
import App from 'components/App'
import 'stylesheets/index.css'

render(
  <StrictMode>
    <App />
  </StrictMode>,
  document.getElementById('root')
)
