import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles'

import routesBuilder from 'routesBuilder'
import { AccountPage } from 'pages/userPanel/account/AccountPage'
import { AdminCreatePage } from 'pages/userPanel/adminManagement/AdminCreatePage'
import { AdminLayout } from './Layouts/AdminLayout'
import { AdminManagementPage } from 'pages/userPanel/adminManagement/AdminManagementPage'
import { Error404Page } from 'pages/errors/Error404Page'
import { HistoryPage } from 'pages/userPanel/HistoryPage'
import { LandingPage } from 'pages/LandingPage'
import { LoginPage } from 'pages/auth/LoginPage'
import { LogoutPage } from 'pages/auth/LogoutPage'
import { NavbarLayout } from 'components/Layouts/NavbarLayout'
import { NotificationProvider } from './Context/NotificationContext'
import { PasswordChangePage } from 'pages/userPanel/account/PasswordChangePage'
import { PaymentsPage } from 'pages/userPanel/PaymentsPage'
import { PublicLayout } from './Layouts/PublicLayout'
import { QuickBuyPage } from 'pages/quickBuy/QuickBuyPage'
import { QuickBuyPanelPage } from 'pages/quickBuy/QuickBuyPanelPage'
import { RegisterPage } from 'pages/auth/RegisterPage'
import { ServerSettingsPage } from 'pages/userPanel/serverSettings/ServerSettingsPage'
import { TransactionFormPage } from 'pages/userPanel/transactions/TransactionFormPage'
import { TransactionsPage } from 'pages/userPanel/transactions/TransactionsPage'
import { UserLayout } from './Layouts/UserLayout'
import { UserProvider } from './Context/UserContext'
import { WalletLayout } from 'components/Layouts/WalletLayout'

const theme = createTheme({
  palette: {
    primary: {
      main: '#A855F7'
    },
    secondary: {
      main: '#db2777'
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
                  element={<QuickBuyPanelPage />}
                />
                <Route
                  path={routesBuilder.userPanel.history}
                  element={<HistoryPage />}
                />
                <Route
                  path={routesBuilder.userPanel.index}
                  element={
                    <Navigate replace to={routesBuilder.userPanel.history} />
                  }
                />
                <Route
                  path={routesBuilder.userPanel.account.index}
                  element={<AccountPage />}
                />
                <Route
                  path={routesBuilder.userPanel.account.password}
                  element={<PasswordChangePage />}
                />
              </Route>

              <Route
                element={<AdminLayout />}
                path={routesBuilder.userPanel.index}
              >
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
                  element={<WalletLayout />}
                />
                <Route
                  path={routesBuilder.userPanel.transactions.index}
                  element={<TransactionsPage />}
                />
                <Route
                  path={routesBuilder.userPanel.transactions.new}
                  element={<TransactionFormPage />}
                />
                <Route
                  path={routesBuilder.userPanel.serverSettings.index}
                  element={<ServerSettingsPage />}
                />
                <Route
                  path={routesBuilder.userPanel.index}
                  element={
                    <Navigate replace to={routesBuilder.userPanel.history} />
                  }
                />
                <Route
                  path={routesBuilder.userPanel.payments}
                  element={<PaymentsPage />}
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
