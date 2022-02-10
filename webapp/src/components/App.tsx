import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'

import { LandingPage } from 'pages/LandingPage'
import { QuickBuyPage } from 'pages/QuickBuyPage'
import routesBuilder from 'routesBuilder'

const theme = createTheme({
  palette: {
    primary: {
      main: '#A855F7'
    },
    secondary: {
      main: '#ffffff'
    }
  }
})

const App = () => {
  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <Routes>
          <Route path={routesBuilder.landingPage} element={<LandingPage />} />
          <Route path={routesBuilder.quickBuy} element={<QuickBuyPage />} />
        </Routes>
      </ThemeProvider>
    </BrowserRouter>
  )
}

export default App
