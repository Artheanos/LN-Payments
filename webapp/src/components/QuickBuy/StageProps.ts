import { PaymentDetails } from 'webService/interface/payment'

export interface StageProps {
  onNext: () => void
  onPrevious: () => void
  setStageIndex?: (index: number) => void
  payment?: PaymentDetails
  setPayment: (payment: PaymentDetails | undefined) => void
  tokens?: string[]
  setTokens: (tokens: string[] | undefined) => void
}
