import { render, screen } from 'tests/test-utils'
import { TokenPopup } from 'components/History/TokenPopup'

describe('TokenPopup', () => {
  it('should properly render token popup', async () => {
    const tokens = ['2137']
    render(
      <TokenPopup
        tokens={tokens}
        anchorEl={document.createElement('div')}
        id="1"
        handleClose={() => false}
      />
    )
    expect(screen.getByText('#0')).toBeInTheDocument()
    expect(screen.getByText('2137')).toBeInTheDocument()
  })
})
