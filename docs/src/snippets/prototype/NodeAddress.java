@Override
public void proceed(String... args) {
    try {
        String nodeKey = api.getInfo().getIdentityPubkey();
        String ip = retrievePublicIp();
        printer.printf("%s@%s:9735", nodeKey, ip);
    } catch (StatusException | ValidationException | IOException e) {
        printer.println("Unable to get node address");
    }
}