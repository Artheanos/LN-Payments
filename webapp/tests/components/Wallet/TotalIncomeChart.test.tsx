import { render, screen } from 'tests/test-utils'
import { TotalIncomeChart } from 'components/wallet/TotalIncomeChart'

describe('TotalIncomeChart', () => {
  beforeAll(() => {
    window.ResizeObserver = jest.fn().mockImplementation(() => ({
      observe: jest.fn(),
      unobserve: jest.fn(),
      disconnect: jest.fn()
    }))
  })

  it('should render', () => {
    const { container } = render(
      <TotalIncomeChart
        chartData={[
          {
            key: '2022-04',
            value: 3
          },
          {
            key: '2022-05',
            value: 2
          },
          {
            key: '2022-06',
            value: 1
          }
        ]}
      />
    )
    expect(screen.getByText('Total income')).toBeInTheDocument()
    expect(container.firstChild!.firstChild!.lastChild).toHaveClass(
      'recharts-responsive-container'
    )
  })

  it('should render error message when not enough elements given', () => {
    render(<TotalIncomeChart chartData={[]} />)
    expect(screen.getByText('Total income')).toBeInTheDocument()
    expect(screen.getByText('No data to show')).toBeInTheDocument()
  })
})
