import bitcoin from 'bitcoin'
import { ECPair } from 'bitcoinjs-lib'
import { toHexBytes } from 'utils/hex'
import { Input } from 'bitcoinjs-lib/types/transaction'
import { Buffer } from 'buffer'

const network = bitcoin.networks.testnet

/**
 * Signs all transaction inputs with the given private key. This is not the limitation, because all the inputs
 * in the system are encoded with the same public keys, so there signature should always be correct.
 *
 * @param rawTx  Raw transaction as hex string.
 * @param privateKey  Private key as hex string.
 * @param redeemScript  Redeem script used in input.
 * @returns  Signed raw transaction.
 */
export const signTx = (
  rawTx: string,
  privateKey: Buffer,
  redeemScript: string,
): string => {
  const tx = bitcoin.Transaction.fromHex(rawTx)
  const builder = bitcoin.TransactionBuilder.fromTransaction(tx, network)
  tx.ins.forEach((input: Input, index: number) => {
    builder.sign(
      index,
      ECPair.fromPrivateKey(privateKey, {
        network: network,
      }),
      toHexBytes(redeemScript),
    )
  })
  try {
    return builder.build().toHex()
  } catch (e) {
    return builder.buildIncomplete().toHex()
  }
}

/**
 * Generates random keypair, which is later used to sign transactions and upload keys to a server.
 *
 * @returns  A pair of keys.
 */
export const generateKeyPair = () => {
  return bitcoin.ECPair.makeRandom({
    network: network,
  })
}
