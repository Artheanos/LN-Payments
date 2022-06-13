// REACT NAVIGATION

import 'react-native-gesture-handler/jestSetup'

jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper')

// ASYNC STORAGE

import mockAsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock'

jest.mock('@react-native-async-storage/async-storage', () => mockAsyncStorage)

jest.mock('react-native-vector-icons/MaterialIcons', () => 'Icon')
