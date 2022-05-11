@Override
public void proceed(String... args) {
    Transaction transaction = new Transaction(walletAppKit.params(), HexFormat.of().parseHex(args[0]));
        try {
        transaction.getInputs().forEach(transactionInput -> {
            Script scriptSig = transactionInput.getScriptSig();
            Script redeemScript = new Script(scriptSig.getChunks().get(scriptSig.getChunks().size() - 1).data);
            Script scriptPubKey = ScriptBuilder.createP2SHOutputScript(redeemScript);
            scriptSig.correctlySpends(transaction, 0, scriptPubKey, Script.ALL_VERIFY_FLAGS);
        });
        walletAppKit.peerGroup().broadcastTransaction(transaction).broadcast().get(1L, TimeUnit.MINUTES);
    } catch (InterruptedException | ExecutionException | ScriptException | TimeoutException e) {
        e.printStackTrace();
    }
}