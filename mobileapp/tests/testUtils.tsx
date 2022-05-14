import { render } from '@testing-library/react-native'
import { NativeBaseProvider } from 'native-base'
import { ReactElement } from 'react'

const inset = {
  frame: { x: 0, y: 0, width: 0, height: 0 },
  insets: { top: 0, left: 0, right: 0, bottom: 0 },
}

const customRender = (children: ReactElement) =>
  render(
    <NativeBaseProvider initialWindowMetrics={inset}>
      {children}
    </NativeBaseProvider>,
  )

export * from '@testing-library/react-native'
export { customRender as render }
