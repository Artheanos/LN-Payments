@Override
public void proceed(String... args) {
    try {
        Address address = Address.fromString(walletAppKit.params(), args[0]);
        Wallet wallet = walletAppKit.wallet();
        Transaction payingToMultisigTx = new Transaction(walletAppKit.params());
        Coin value = Coin.valueOf(100);
        Coin total = Coin.ZERO;
        List<TransactionOutput> watchedOutputs = wallet.getWatchedOutputs(true);
        for (TransactionOutput watchedOutput : watchedOutputs) {
            if (watchedOutput.getScriptPubKey().getToAddress(walletAppKit.params()).equals(address)) {
                payingToMultisigTx.addInput(watchedOutput);
                total = total.add(watchedOutput.getValue());
            }
        }
        TransactionOutput output = new TransactionOutput(walletAppKit.params(), payingToMultisigTx, value, Address.fromString(walletAppKit.params(), args[1]));
        payingToMultisigTx.addOutput(output);
        TransactionOutput returnRest = new TransactionOutput(walletAppKit.params(), payingToMultisigTx,
        total.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE).subtract(value), address);
        payingToMultisigTx.addOutput(returnRest);
        printer.println(payingToMultisigTx.toHexString());
    } catch (Exception e) {
        e.printStackTrace();
    }
}