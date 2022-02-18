export interface StageProps {
  onNext: () => void
  onPrevious: () => void
  setStageIndex?: (index: number) => void
  payment: PaymentDetails | null
  setPayment: (payment: PaymentDetails | null) => void
}
