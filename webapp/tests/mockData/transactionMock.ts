import { TransactionStatus } from 'webService/interface/transaction'

export const transactionMock = (status: TransactionStatus) => {
  return {
    sourceAddress: '2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv',
    targetAddress: '2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4',
    value: 1100,
    approvals: 0,
    requiredApprovals: 2,
    status: status,
    dateCreated: '2022-05-03T17:36:10.518877Z'
  }
}
