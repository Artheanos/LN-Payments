@Override
public void proceed(String... args) {
    try {
        Wallet wallet = walletAppKit.wallet();
        List<ECKey> keys = Arrays.stream(args).map(e -> ECKey.fromPublicOnly(HexFormat.of().parseHex(e))).toList();
        wallet.importKeys(keys);
        Script redeemScript = ScriptBuilder.createRedeemScript(2, keys);
        Script script = ScriptBuilder.createP2SHOutputScript(redeemScript);
        Address address = script.getToAddress(walletAppKit.params());
        wallet.addWatchedAddress(address);
        printer.println(address + " " + HexFormat.of().formatHex(script.getProgram()));
    } catch (Exception e) {
        e.printStackTrace();
    }
}