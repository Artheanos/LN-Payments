let tx = bitcoin.Transaction.fromHex(params[0])
let builder = bitcoin.TransactionBuilder.fromTransaction(tx, testnet)
tx.ins.forEach((input, index) => {
    let privateKey = ECPair.fromPrivateKey(Buffer.from(hexToBytes(params[1]), {network: testnet}))
    builder.sign(index, privateKey, Buffer.from(hexToBytes(params[2])))
})
console.log(builder.buildIncomplete().toHex())