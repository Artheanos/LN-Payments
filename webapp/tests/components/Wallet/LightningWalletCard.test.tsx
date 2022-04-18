import { render, screen } from 'tests/test-utils'
import { LightningWalletCard } from 'components/wallet/LightningWalletCard'

describe('LightningWalletCard', () => {
  it('should render', () => {
    render(
      <LightningWalletCard
        unconfirmedBalance={100}
        availableBalance={1600}
        autoTransferLimit={10000}
      />
    )
    expect(screen.getByText('Lightning Wallet')).toBeInTheDocument()
    expect(screen.getByText('1,600 sats')).toBeInTheDocument()
    expect(screen.getByText('0')).toBeInTheDocument()
    expect(screen.getByText('10,000')).toBeInTheDocument()
    expect(screen.getByText('100 sats unconfirmed')).toBeInTheDocument()
  })
})
