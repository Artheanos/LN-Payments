// REACT NAVIGATION

import 'react-native-gesture-handler/jestSetup'

// jest.mock('react-native-reanimated', () => {
//   // eslint-disable-next-line @typescript-eslint/no-var-requires
//   const Reanimated = require('react-native-reanimated/mock')
//
//   Reanimated.default.call = () => {}
//
//   return Reanimated
// })

jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper')

// ASYNC STORAGE

import mockAsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock'

jest.mock('@react-native-async-storage/async-storage', () => mockAsyncStorage)
