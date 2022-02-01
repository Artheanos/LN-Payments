import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

import routesBuilder from 'routesBuilder'

export const LandingPage = () => {
  const { t } = useTranslation('common')

  return (
    <div>
      <div className="py-16 px-4 mx-auto max-w-screen-xl sm:py-24 sm:px-6 lg:px-8">
        <div className="text-center">
          <h2 className="text-lg font-semibold tracking-wide text-purple-900 uppercase">
            Welcome to
          </h2>
          <p className="my-3 text-4xl font-bold text-gray-900 sm:text-5xl sm:tracking-tight lg:text-6xl">
            {t('title')}
          </p>
          <p className="text-xl text-gray-900">Very cool!</p>
          <div className="mt-20">
            <Link to={routesBuilder.quickBuy}>
              <button className="btn">QuickBuy</button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
