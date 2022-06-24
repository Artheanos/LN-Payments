// REACT NAVIGATION

import 'react-native-gesture-handler/jestSetup'

jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper')

// ASYNC STORAGE

import mockAsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock'

jest.mock('@react-native-async-storage/async-storage', () => mockAsyncStorage)

// THEMES

jest.mock('react-native-vector-icons/MaterialIcons', () => 'Icon')
jest.mock('native-base/src/theme/base/colors', () => 'colors')

// NOTIFEE

jest.mock('@notifee/react-native', () => {})
