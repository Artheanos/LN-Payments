import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'

import routesBuilder from 'routesBuilder'
import { LandingPage } from 'pages/LandingPage'
import { LoginPage } from 'pages/auth/LoginPage'
import { NavbarLayout } from 'components/Layouts/NavbarLayout'
import { QuickBuyPage } from 'pages/quickBuy/QuickBuyPage'
import { RegisterPage } from 'pages/auth/RegisterPage'
import { SnackbarProvider } from 'components/Layouts/GlobalSnackbar'

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
        <SnackbarProvider>
          <Routes>
            <Route element={<NavbarLayout />}>
              <Route
                path={routesBuilder.landingPage}
                element={<LandingPage />}
              />
              <Route path={routesBuilder.login} element={<LoginPage />} />
              <Route path={routesBuilder.register} element={<RegisterPage />} />
              <Route path={routesBuilder.quickBuy} element={<QuickBuyPage />} />
            </Route>
          </Routes>
        </SnackbarProvider>
      </ThemeProvider>
    </BrowserRouter>
  )
}

export default App
