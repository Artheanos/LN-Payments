import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'

import routesBuilder from 'routesBuilder'
import { LandingPage } from 'pages/LandingPage'
import { LoginPage } from 'pages/auth/LoginPage'
import { NavbarLayout } from 'components/Layouts/NavbarLayout'
import { QuickBuyPage } from 'pages/quickBuy/QuickBuyPage'
import { RegisterPage } from 'pages/auth/RegisterPage'
import { NotificationProvider } from './Context/NotificationContext'
import { UserProvider } from './Context/UserContext'
import { UserLayout } from './Layouts/UserLayout'

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
        <UserProvider>
          <NotificationProvider>
            <Routes>
              <Route element={<NavbarLayout />}>
                <Route element={<UserLayout />}>
                  <Route
                    path={routesBuilder.quickBuy}
                    element={<QuickBuyPage />}
                  />
                </Route>
                <Route
                  path={routesBuilder.landingPage}
                  element={<LandingPage />}
                />
                <Route
                  path={routesBuilder.register}
                  element={<RegisterPage />}
                />
                <Route path={routesBuilder.login} element={<LoginPage />} />
              </Route>
            </Routes>
          </NotificationProvider>
        </UserProvider>
      </ThemeProvider>
    </BrowserRouter>
  )
}

export default App
