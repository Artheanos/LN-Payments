import { render, screen } from 'tests/test-utils'
import { PaymentEntry } from 'components/History/PaymentEntry'
import { waitFor } from '@testing-library/react'
import { PaymentStatus } from 'webService/interface/payment'

describe('PaymentEntry', () => {
  it('should properly render entry with no tokens', () => {
    render(
      <table>
        <tbody>
          <PaymentEntry
            paymentRequest="aaa"
            timestamp={new Date('2022-03-21 18:23:34.543684')}
            expirationTimestamp={new Date('2022-03-21 18:23:34.543684')}
            price={2137}
            numberOfTokens={1}
            paymentStatus={PaymentStatus.CANCELLED}
            tokens={[]}
            paymentTopic="ddd"
          />
        </tbody>
      </table>
    )
    expect(screen.getByText('aaa')).toBeInTheDocument()
    expect(
      screen.getByText(new Date('2022-03-21 18:23:34.543684').toLocaleString())
    ).toBeInTheDocument()
    expect(screen.getByText('2137')).toBeInTheDocument()
    expect(screen.getByText('CANCELLED')).toBeInTheDocument()
    expect(screen.getByText('none')).toBeInTheDocument()
    expect(screen.queryByText('Show')).not.toBeInTheDocument()
  })

  it('should show popup with token on click', async () => {
    render(
      <table>
        <tbody>
          <PaymentEntry
            paymentRequest="aaa"
            timestamp={new Date('2022-03-21 18:23:34.543684')}
            expirationTimestamp={new Date('2022-03-21 18:23:34.543684')}
            price={2137}
            numberOfTokens={1}
            paymentStatus={PaymentStatus.COMPLETE}
            tokens={['ddd']}
            paymentTopic="ddd"
          />
        </tbody>
      </table>
    )
    expect(screen.getByText('COMPLETE')).toBeInTheDocument()
    screen.getByText('Show').click()
    await waitFor(() => {
      expect(screen.getByText('#0')).toBeInTheDocument()
      expect(screen.getByText('ddd')).toBeInTheDocument()
    })
  })
})
