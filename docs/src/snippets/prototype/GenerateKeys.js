const keyPair = ECPair.makeRandom({network: testnet});
console.log(toHexString(keyPair.privateKey), toHexString(keyPair.publicKey))