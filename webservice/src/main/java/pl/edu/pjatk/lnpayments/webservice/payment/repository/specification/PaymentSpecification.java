package pl.edu.pjatk.lnpayments.webservice.payment.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.edu.pjatk.lnpayments.webservice.common.entity.User;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment_;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.enums.SearchableField;

import javax.persistence.criteria.*;

public class PaymentSpecification implements Specification<Payment> {

    private final SearchableField field;
    private final String value;

    public PaymentSpecification(SearchableField field, String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return switch (field.getTable()) {
            case USER -> {
                Join<Payment, User> tableJoin = root.join(Payment_.USER);
                yield criteriaBuilder.equal(tableJoin.get(field.getField()), value);
            }
            case PAYMENT -> criteriaBuilder.equal(root.get(field.getField()), value);
        };
    }
}
