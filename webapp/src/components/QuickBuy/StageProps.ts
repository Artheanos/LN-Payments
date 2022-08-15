import { PaymentDetails, PaymentInfo } from 'webService/interface/payment'

export interface StageProps {
  onNext: () => void
  onPrevious: () => void
  setStageIndex?: (index: number) => void
  payment?: PaymentDetails
  paymentInfo: PaymentInfo
  setPayment: (payment: PaymentDetails | undefined) => void
  tokens?: string[]
  setTokens: (tokens: string[] | undefined) => void
}
