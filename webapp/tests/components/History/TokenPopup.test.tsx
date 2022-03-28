import { render, screen } from '../../test-utils'
import { TokenPopup } from '../../../src/components/History/TokenPopup'

describe('TokenPopup', () => {
  it('should properly render token popup', async () => {
    const tokens = ['2137']
    render(
      <TokenPopup
        tokens={tokens}
        anchorEl={<div />}
        id="1"
        handleClose={() => false}
      />
    )
    expect(screen.getByText('#0')).toBeInTheDocument()
    expect(screen.getByText('2137')).toBeInTheDocument()
  })
})
