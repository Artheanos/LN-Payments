import { render, screen } from 'tests/test-utils'
import { BitcoinWalletCard } from 'components/wallet/BitcoinWalletCard'

describe('BitcoinWalletCard', () => {
  it('should render children', () => {
    render(
      <BitcoinWalletCard
        currentReferenceFee={1000}
        availableBalance={21}
        unconfirmedBalance={37}
      />
    )
    expect(screen.getByText('Bitcoin Wallet')).toBeInTheDocument()
    expect(screen.getByText('21 sats')).toBeInTheDocument()
    expect(screen.getByText('37 sats unconfirmed')).toBeInTheDocument()
  })
})
