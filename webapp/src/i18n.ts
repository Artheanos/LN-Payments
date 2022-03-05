import { initReactI18next } from 'react-i18next'
import i18n from 'i18next'

import auth from 'locales/en/auth.json'
import common from 'locales/en/common.json'

const resources = {
  en: {
    auth,
    common
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
