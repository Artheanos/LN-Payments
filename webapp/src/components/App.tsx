import { BrowserRouter, Route, Routes } from 'react-router-dom'

import { LandingPage } from 'pages/LandingPage'
import { QuickBuyPage } from 'pages/QuickBuyPage'
import routesBuilder from 'routesBuilder'

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={routesBuilder.landingPage} element={<LandingPage />} />
        <Route path={routesBuilder.quickBuy} element={<QuickBuyPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
