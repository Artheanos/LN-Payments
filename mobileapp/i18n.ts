import { initReactI18next } from 'react-i18next'
import i18n from 'i18next'

import { common } from 'common-ts/dist/i18n'

import auth from 'locales/en/auth.json'
import sidebar from 'locales/en/sidebar.json'

const resources = {
  en: {
    auth,
    common,
    sidebar,
  },
}

i18n.use(initReactI18next).init({
  resources,
  compatibilityJSON: 'v3',
  lng: 'en',
  keySeparator: '.',
  defaultNS: 'common',
  interpolation: { escapeValue: false },
  react: { useSuspense: false },
})

export default i18n
