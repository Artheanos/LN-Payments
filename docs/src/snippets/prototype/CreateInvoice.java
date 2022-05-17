@Override
public void proceed(String... args) {
    try {
        int amount = Integer.parseInt(args[0]);
        String memo = args[1];
        Invoice invoice = new Invoice();
        invoice.setMemo(memo);
        invoice.setValue(value);
        api.addInvoice(invoice);
    } catch (StatusException | ValidationException e) {
        printer.println("Unable to generate invoice");
    }
}