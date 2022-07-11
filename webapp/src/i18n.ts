import { initReactI18next } from 'react-i18next'
import i18n from 'i18next'

import adminManagement from 'locales/en/adminManagement.json'
import auth from 'locales/en/auth.json'
import common from 'locales/en/common.json'
import history from 'locales/en/history.json'
import quickBuy from 'locales/en/quickBuy.json'
import transactions from 'locales/en/transactions.json'
import wallet from 'locales/en/wallet.json'
import settings from 'locales/en/settings.json'
import account from 'locales/en/account.json'

const resources = {
  en: {
    adminManagement,
    auth,
    common,
    history,
    wallet,
    quickBuy,
    transactions,
    settings,
    account
  }
}

i18n.use(initReactI18next).init({
  resources,
  lng: 'en',
  keySeparator: '.',
  defaultNS: 'common',
  interpolation: { escapeValue: false },
  react: { useSuspense: false }
})

export default i18n
