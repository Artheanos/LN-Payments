public boolean verifySignature(Transaction transaction, ECKey key) {
    for (TransactionInput transactionInput : transaction.getInputs()) {
        Script scriptSig = transactionInput.getScriptSig();
        List<ScriptChunk> chunks = scriptSig.getChunks();
        if (chunks.size() > 2) {
            Script redeemScript = new Script(chunks.get(chunks.size() - 1).data);
            List<ECKey> keys = redeemScript.getPubKeys();
            Sha256Hash msg = transaction.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
            if (keys.size() == chunks.size() - 2 && keys.contains(key)) {
                for (int x = 1; x < chunks.size() - 1; x++) {
                    try {
                        byte[] signature = chunks.get(x).data;
                        ECKey.ECDSASignature ecdsaSignature = TransactionSignature.decodeFromDER(signature);
                        boolean verify = key.verify(msg, ecdsaSignature);
                        if (verify) return true;
                    } catch (SignatureDecodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    return false;
}