import { render, screen } from 'tests/test-utils'
import { ChannelsBalanceCard } from 'components/wallet/ChannelsBalanceCard'

describe('ChannelsBalanceCard', () => {
  it('should render children', () => {
    render(
      <ChannelsBalanceCard
        autoChannelCloseLimit={10000}
        openedChannels={3}
        totalBalance={2137}
      />
    )
    expect(screen.getByText('Channels Balance')).toBeInTheDocument()
    expect(screen.getByText('2,137 sats')).toBeInTheDocument()
    expect(screen.getByText('0')).toBeInTheDocument()
    expect(screen.getByText('10,000')).toBeInTheDocument()
    expect(screen.getByText('3 open channels')).toBeInTheDocument()
  })
})
