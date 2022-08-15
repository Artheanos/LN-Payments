import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'

export const LandingPage = () => {
  const { t } = useTranslation('common')

  return (
    <div className="flex justify-center items-center py-16 px-8 w-screen sm:py-32 sm:px-6 lg:px-8">
      <div className="text-center">
        <h2 className="text-lg font-semibold tracking-wide text-purple-900 uppercase">
          Welcome to
        </h2>
        <p className="my-3 text-4xl font-bold text-gray-900 sm:text-5xl sm:tracking-tight lg:text-6xl">
          {t('title')}
        </p>
        <p className="text-xl text-gray-900">
          System for receiving payments and generating single-use tokens.
        </p>
        <div className="mt-20 space-x-16">
          <Link to={routesBuilder.login}>
            <button className="btn">Login</button>
          </Link>
          <Link to={routesBuilder.register}>
            <button className="btn">Register</button>
          </Link>
          <Link to={routesBuilder.quickBuy}>
            <button className="btn">QuickBuy</button>
          </Link>
        </div>
      </div>
    </div>
  )
}
