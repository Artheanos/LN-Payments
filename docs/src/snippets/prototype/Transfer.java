@Override
public void proceed(String... args) {
    printer.println(kit.wallet().currentReceiveAddress().toString());
    try {
        SendCoinsRequest req = new SendCoinsRequest();
        req.setSendAll(true);
        req.setAddr(kit.wallet().currentReceiveAddress().toString());
        SendCoinsResponse sendCoinsResponse = synchronousLndAPI.sendCoins(req);
        printer.println(sendCoinsResponse.getTxid());
    } catch (StatusException | ValidationException e) {
        e.printStackTrace();
    }
}