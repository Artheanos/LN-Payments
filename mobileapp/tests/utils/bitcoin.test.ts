import { generateKeyPair, signTx } from 'utils/bitcoin'

describe('bitcoin utils', () => {
  it('should generate random keys', () => {
    const pair = generateKeyPair()
    expect(pair.privateKey).not.toBeFalsy()
    expect(pair.publicKey).not.toBeFalsy()
    expect(pair.publicKey).not.toBe(pair.privateKey)
  })

  it('should sign transaction', () => {
    const rawTx =
      '01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000000ffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000'
    const privKey =
      'b4cff81a80b341efad81b67fb6f3c23d4f1f4d63ba743f59c987e437f40324b7'
    const redeemScript =
      '522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae'

    const signedTx = signTx(rawTx, privKey, redeemScript)
    expect(signedTx).toBe(
      '01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000',
    )
  })
})
