package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.service.InvoiceService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.NodeDetailsService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;

import java.util.List;

@Service
public class PaymentFacade {

    private final InvoiceService invoiceService;
    private final PropertyService propertyService;
    private final PaymentDataService paymentDataService;
    private final NodeDetailsService nodeDetailsService;

    @Autowired
    public PaymentFacade(InvoiceService invoiceService, PropertyService propertyService,
                         PaymentDataService paymentDataService, NodeDetailsService nodeDetailsService) {
        this.invoiceService = invoiceService;
        this.propertyService = propertyService;
        this.paymentDataService = paymentDataService;
        this.nodeDetailsService = nodeDetailsService;
    }

    public PaymentInfo buildInfoResponse() {
        List<Payment> pendingPayments = paymentDataService.findPendingPaymentsByUser();
        return PaymentInfo.builder()
                .nodeUrl(nodeDetailsService.getNodeUrl())
                .description(propertyService.getDescription())
                .price(propertyService.getPrice())
                .pendingPayments(pendingPayments)
                .build();
    }

    public Payment createNewPayment(PaymentDetailsRequest paymentDetailsRequest) {
        int paymentExpiryInSeconds = propertyService.getPaymentExpiryInSeconds();
        String paymentRequest = invoiceService.createInvoice(
                paymentDetailsRequest.getNumberOfTokens(),
                propertyService.getPrice(),
                paymentExpiryInSeconds,
                propertyService.getInvoiceMemo()
        );
        Payment payment = new Payment(paymentRequest, paymentExpiryInSeconds, PaymentStatus.PENDING);
        return paymentDataService.savePayment(payment);
    }

}
