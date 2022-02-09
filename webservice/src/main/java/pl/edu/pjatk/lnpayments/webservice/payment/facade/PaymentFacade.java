package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.repository.PaymentRepository;
import pl.edu.pjatk.lnpayments.webservice.payment.service.InvoiceService;

@Service
public class PaymentFacade {

    private final InvoiceService invoiceService;
    private final PropertyService propertyService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentFacade(InvoiceService invoiceService, PropertyService propertyService, PaymentRepository paymentRepository) {
        this.invoiceService = invoiceService;
        this.propertyService = propertyService;
        this.paymentRepository = paymentRepository;
    }

    public Payment createNewPayment(PaymentDetailsRequest paymentDetailsRequest) {
        int paymentExpiryInSeconds = propertyService.getPaymentExpiryInSeconds();
        String paymentRequest = invoiceService.createInvoice(
                paymentDetailsRequest.getNumberOfTokens(),
                propertyService.getPrice(),
                paymentExpiryInSeconds,
                propertyService.getInvoiceMemo()
        );
        Payment payment = new Payment(paymentRequest, paymentExpiryInSeconds);
        return paymentRepository.save(payment);
    }
}
