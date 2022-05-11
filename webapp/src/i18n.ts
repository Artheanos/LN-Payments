import { initReactI18next } from 'react-i18next'
import i18n from 'i18next'

import { common } from 'common-ts/dist/i18n'

import adminManagement from 'locales/en/adminManagement.json'
import auth from 'locales/en/auth.json'
import history from 'locales/en/history.json'
import wallet from 'locales/en/wallet.json'
import quickBuy from 'locales/en/quickBuy.json'

const resources = {
  en: {
    adminManagement,
    auth,
    common,
    history,
    wallet,
    quickBuy
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
