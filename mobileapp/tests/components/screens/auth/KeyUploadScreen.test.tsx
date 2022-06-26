import React from 'react'
import { render, waitFor, screen } from '@testing-library/react-native'
import { HelperComponent } from 'tests/concern/HelperComponent'
import { KeyUploadScreen } from 'components/screens/auth/KeyUploadScreen'
import { api } from 'webService/requests'
import AsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock'
import { UserContext } from 'components/context/UserContext'
import { Alert } from 'react-native'

jest.spyOn(Alert, 'alert')

jest.mock('utils/bitcoin', () => ({
  generateKeyPair: () => ({
    privateKey: '123',
    publicKey: '456',
  }),
}))

jest.mock('webService/requests', () => ({
  api: {
    admins: {
      uploadKeys: jest.fn(),
    },
  },
}))

const apiMock = api.admins.uploadKeys as jest.Mock

describe('KeyUploadScreen', () => {
  const updateUserMock = jest.fn()

  beforeEach(() => {
    updateUserMock.mockClear()
    render(
      <HelperComponent>
        <UserContext.Provider
          value={
            { user: { email: 'user1' }, updateUser: updateUserMock } as never
          }
        >
          <KeyUploadScreen />
        </UserContext.Provider>
      </HelperComponent>,
    )
  })

  describe('when the api does not respond', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(new Promise(() => {}))
    })

    it('displays proper message', async () => {
      await waitFor(() => {
        expect(screen.getByText('Uploading keys')).toBeDefined()
      })
    })
  })

  describe('when api responds with status 200', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(
        new Promise((resolve) => resolve({ status: 200 })),
      )
    })

    it('displays messages', async () => {
      await waitFor(() => {
        expect(AsyncStorage.setItem).toBeCalledWith(
          'user1',
          '{"publicKey":"456","privateKey":"123"}',
        )
        expect(screen.getByText('Redirecting')).toBeDefined()
      })
    })
  })

  describe('when api responds with status 409', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(
        new Promise((resolve) => resolve({ status: 409 })),
      )
    })

    it('alerts with proper message', async () => {
      await waitFor(() => {
        expect(Alert.alert).toHaveBeenCalledWith('Key already uploaded')
        expect(updateUserMock).toHaveBeenCalledWith({
          token: null,
          email: null,
          uploadKeys: false,
        })
      })
    })
  })

  describe('when api responds with status 400', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(
        new Promise((resolve) => resolve({ status: 400 })),
      )
    })

    it('alerts with proper message', async () => {
      await waitFor(() => {
        expect(Alert.alert).toHaveBeenCalledWith('Error while uploading keys')
        expect(updateUserMock).toHaveBeenCalledWith({
          token: null,
          email: null,
          uploadKeys: false,
        })
      })
    })
  })
})
