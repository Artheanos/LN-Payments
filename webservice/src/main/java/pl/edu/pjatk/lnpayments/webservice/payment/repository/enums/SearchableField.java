package pl.edu.pjatk.lnpayments.webservice.payment.repository.enums;

import pl.edu.pjatk.lnpayments.webservice.common.entity.User_;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment_;

public enum SearchableField {

    EMAIL(SearchableTable.USER, User_.EMAIL),
    NUMBER_OF_TOKENS(SearchableTable.PAYMENT, Payment_.NUMBER_OF_TOKENS);

    private final SearchableTable table;
    private final String field;

    SearchableField(SearchableTable table, String field) {
        this.table = table;
        this.field = field;
    }

    public SearchableTable getTable() {
        return table;
    }

    public String getField() {
        return field;
    }

}
