import { render, screen } from 'tests/test-utils'
import { WalletCard } from 'components/wallet/WalletCard'

describe('WalletCard', () => {
  it('should render children', () => {
    render(
      <WalletCard standardSize={12}>
        <p>test</p>
      </WalletCard>
    )
    expect(screen.getByText('test')).toBeInTheDocument()
  })
})
