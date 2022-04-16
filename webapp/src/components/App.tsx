import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'

import routesBuilder from 'routesBuilder'
import { AdminCreatePage } from 'pages/userPanel/adminManagement/AdminCreatePage'
import { AdminLayout } from './Layouts/AdminLayout'
import { AdminManagementPage } from 'pages/userPanel/adminManagement/AdminManagementPage'
import { Error404Page } from 'pages/errors/Error404Page'
import { History } from './History/History'
import { LandingPage } from 'pages/LandingPage'
import { LoginPage } from 'pages/auth/LoginPage'
import { LogoutPage } from 'pages/auth/LogoutPage'
import { NavbarLayout } from 'components/Layouts/NavbarLayout'
import { NotificationProvider } from './Context/NotificationContext'
import { PublicLayout } from './Layouts/PublicLayout'
import { QuickBuyPage } from 'pages/quickBuy/QuickBuyPage'
import { RegisterPage } from 'pages/auth/RegisterPage'
import { UserLayout } from './Layouts/UserLayout'
import { UserProvider } from './Context/UserContext'
import { WalletPage } from '../pages/userPanel/wallet/WalletPage'
import { WalletCreatePage } from 'pages/userPanel/wallet/WalletCreatePage'

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

const App = () => (
  <BrowserRouter>
    <ThemeProvider theme={theme}>
      <UserProvider>
        <NotificationProvider>
          <Routes>
            <Route element={<NavbarLayout />}>
              <Route
                element={<UserLayout />}
                path={routesBuilder.userPanel.index}
              >
                <Route
                  path={routesBuilder.userPanel.quickBuy}
                  element={<QuickBuyPage />}
                />
                <Route
                  path={routesBuilder.userPanel.history}
                  element={<History />}
                />
                <Route
                  path={routesBuilder.userPanel.index}
                  element={
                    <Navigate replace to={routesBuilder.userPanel.history} />
                  }
                />
              </Route>

              <Route
                element={<AdminLayout />}
                path={routesBuilder.userPanel.index}
              >
                <Route
                  path={routesBuilder.userPanel.history}
                  element={<History />}
                />
                <Route
                  path={routesBuilder.userPanel.admins.index}
                  element={<AdminManagementPage />}
                />
                <Route
                  path={routesBuilder.userPanel.admins.create}
                  element={<AdminCreatePage />}
                />
                <Route
                  path={routesBuilder.userPanel.wallet.index}
                  element={<WalletPage />}
                />
                <Route
                  path={routesBuilder.userPanel.wallet.new}
                  element={<WalletCreatePage />}
                />
                <Route
                  path={routesBuilder.userPanel.index}
                  element={
                    <Navigate replace to={routesBuilder.userPanel.history} />
                  }
                />
              </Route>

              <Route element={<PublicLayout />}>
                <Route
                  path={routesBuilder.landingPage}
                  element={<LandingPage />}
                />
                <Route
                  path={routesBuilder.quickBuy}
                  element={<QuickBuyPage />}
                />
                <Route
                  path={routesBuilder.register}
                  element={<RegisterPage />}
                />
                <Route path={routesBuilder.login} element={<LoginPage />} />
              </Route>
            </Route>
            <Route path={routesBuilder.logout} element={<LogoutPage />} />
            <Route path="*" element={<Error404Page />} />
          </Routes>
        </NotificationProvider>
      </UserProvider>
    </ThemeProvider>
  </BrowserRouter>
)

export default App
