import React from 'react'
import { render, waitFor, screen } from '@testing-library/react-native'

import { HelperComponent } from 'tests/concern/HelperComponent'
import {
  NotificationDetails,
  NotificationStatus,
  NotificationType,
} from 'webService/interface/notification'
import { NotificationListScreen } from 'components/screens/notification/NotificationListScreen/NotificationListScreen'
import { api } from 'webService/requests'

jest.mock('webService/requests', () => ({
  api: {
    notifications: {
      getUserNotifications: jest.fn(),
    },
  },
}))

const mockNotifications: NotificationDetails[] = [
  {
    id: '1',
    type: NotificationType.TRANSACTION,
    message: 'Notification 1',
    address: 'Address 1',
    status: NotificationStatus.PENDING,
    amount: 1000,
  },
  {
    id: '2',
    type: NotificationType.WALLET_RECREATION,
    message: 'Notification 2',
    address: 'Address 2',
    status: NotificationStatus.DENIED,
    amount: 2000,
  },
]

type ApiResponseData = Awaited<
  ReturnType<typeof api.notifications.getUserNotifications>
>

const apiMock = api.notifications.getUserNotifications as jest.Mock

describe('NotificationListScreen', () => {
  beforeEach(() => {
    render(
      <HelperComponent>
        <NotificationListScreen navigation={{ navigate: jest.fn() } as never} />
      </HelperComponent>,
    )
  })

  describe('when api returns data', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(
        new Promise((resolve) =>
          resolve({
            data: {
              last: true,
              content: mockNotifications,
            },
          } as ApiResponseData),
        ),
      )
    })

    it('displays notifications', async () => {
      await waitFor(() => {
        expect(screen.queryByText('Notification 1')).toBeTruthy()
        expect(screen.queryByText('TRANSACTION')).toBeTruthy()
        expect(screen.queryByText('Notification 2')).toBeTruthy()
        expect(screen.queryByText('WALLET_RECREATION')).toBeTruthy()

        expect(screen.queryByLabelText('loading')).toBeFalsy()
      })
    })
  })

  describe('when api is stuck', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(new Promise(() => {}))
    })

    it('displays loading', async () => {
      await waitFor(() => {
        expect(screen.queryByLabelText('loading')).toBeTruthy()
      })
    })
  })

  describe('when api returns empty list', () => {
    beforeAll(() => {
      apiMock.mockReturnValue(
        new Promise((resolve) =>
          resolve({
            data: { last: true, content: [] },
          }),
        ),
      )
    })

    it('shows proper message', async () => {
      await waitFor(() => {
        expect(screen.queryByText('Nothing there yet')).toBeTruthy()
      })
    })
  })
})
