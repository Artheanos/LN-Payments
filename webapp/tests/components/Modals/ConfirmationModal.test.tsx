import { render, screen } from 'tests/test-utils'
import { ConfirmationModal } from 'components/Modals/ConfirmationModal'

describe('ConfirmationModal', () => {
  let open: boolean
  let setOpen: jest.Mock
  let onConfirm: jest.Mock
  beforeEach(() => {
    open = true
    setOpen = jest.fn()
    onConfirm = jest.fn()
    render(
      <>
        <ConfirmationModal
          message={'MESSAGE#1'}
          open={open}
          setOpen={setOpen}
          onConfirm={onConfirm}
          confirmButtonContent={'CONFIRM'}
        />
        <div id="foo">foo</div>
      </>
    )
  })

  it('renders message', () => {
    expect(screen.getByText('MESSAGE#1')).toBeInTheDocument()
  })

  it('hides after clicking on confirmation button', () => {
    screen.getByText('CONFIRM').click()
    expect(setOpen.mock.calls[0][0]).toBe(false)
    expect(onConfirm.mock.calls.length).toBe(1)
  })
})
