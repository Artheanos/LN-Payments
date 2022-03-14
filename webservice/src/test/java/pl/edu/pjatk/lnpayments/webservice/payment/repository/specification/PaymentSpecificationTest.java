package pl.edu.pjatk.lnpayments.webservice.payment.repository.specification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment_;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.enums.SearchableField;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentSpecificationTest {

    @Mock
    private Root<Payment> rootMock;

    @Mock
    private CriteriaBuilder builderMock;

    @Mock
    private CriteriaQuery<?> queryMock;

    @Test
    void shouldReturnPredicateForEmailAndJoinTables() {
        String email = "test@test.pl";
        PaymentSpecification paymentSpecification = new PaymentSpecification(SearchableField.EMAIL, email);
        Join join = Mockito.mock(Join.class);
        when(rootMock.join(Payment_.USER)).thenReturn(join);

        paymentSpecification.toPredicate(rootMock, queryMock, builderMock);

        verify(rootMock).join(Payment_.USER);
        verify(builderMock).equal(any(), eq(email));
        verify(join).get(SearchableField.EMAIL.getField());
    }

    @Test
    void shouldReturnPredicateAndQueryPayment() {
        String number = "1";
        PaymentSpecification paymentSpecification = new PaymentSpecification(SearchableField.NUMBER_OF_TOKENS, number);

        paymentSpecification.toPredicate(rootMock, queryMock, builderMock);

        verify(rootMock, never()).join(any(SingularAttribute.class));
        verify(rootMock).get(SearchableField.NUMBER_OF_TOKENS.getField());
        verify(builderMock).equal(any(), eq(number));
    }
}
