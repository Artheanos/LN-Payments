package pl.edu.pjatk.lnpayments.webservice.payment.facade;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.model.PaymentInfo;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.Payment;
import pl.edu.pjatk.lnpayments.webservice.payment.model.entity.PaymentStatus;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.PaymentSocketController;
import pl.edu.pjatk.lnpayments.webservice.payment.resource.dto.PaymentDetailsRequest;
import pl.edu.pjatk.lnpayments.webservice.payment.service.InvoiceService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.NodeDetailsService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.PaymentDataService;
import pl.edu.pjatk.lnpayments.webservice.payment.service.TokenService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PaymentFacade {

    private final InvoiceService invoiceService;
    private final PropertyService propertyService;
    private final PaymentDataService paymentDataService;
    private final NodeDetailsService nodeDetailsService;
    private final TokenService tokenService;
    private final PaymentSocketController paymentSocketController;

    @Autowired
    PaymentFacade(InvoiceService invoiceService,
                  PropertyService propertyService,
                  PaymentDataService paymentDataService,
                  NodeDetailsService nodeDetailsService,
                  TokenService tokenService,
                  PaymentSocketController paymentSocketController) {
        this.invoiceService = invoiceService;
        this.propertyService = propertyService;
        this.paymentDataService = paymentDataService;
        this.nodeDetailsService = nodeDetailsService;
        this.tokenService = tokenService;
        this.paymentSocketController = paymentSocketController;
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
        int price = propertyService.getPrice();
        int numberOfTokens = paymentDetailsRequest.getNumberOfTokens();
        String paymentRequest = invoiceService.createInvoice(
                numberOfTokens,
                price,
                paymentExpiryInSeconds,
                propertyService.getInvoiceMemo()
        );
        Payment payment = new Payment(
                paymentRequest,
                numberOfTokens,
                price,
                paymentExpiryInSeconds,
                PaymentStatus.PENDING);
        return paymentDataService.savePayment(payment);
    }

    @Transactional
    public void finalizePayment(String paymentRequest) {
        Payment payment = paymentDataService.findPaymentByRequest(paymentRequest);
        payment.assignTokens(tokenService.generateTokens(payment));
        payment.setStatus(PaymentStatus.COMPLETE);
        String paymentHash = DigestUtils.sha256Hex(paymentRequest);
        paymentSocketController.sendTokens(paymentHash.substring(0, 8), payment.getTokens());
    }

}
