package pl.edu.pjatk.lnpayments.webservice.payment.config;

import org.lightningj.lnd.wrapper.*;
import org.lightningj.lnd.wrapper.message.InvoiceSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pjatk.lnpayments.webservice.payment.observer.InvoiceObserver;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;

import javax.net.ssl.SSLException;
import java.io.File;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.USER_HOME;

@Configuration
class LndConfig {

    private final String host;
    private final int port;
    private final String certificatePath;
    private final String macaroonPath;
    private final String walletDirectory;

    @Autowired
    LndConfig(
            @Value("${lnp.lnd.host}") String host,
            @Value("${lnp.lnd.port}") int port,
            @Value("${lnp.lnd.certificatePath}") String certificatePath,
            @Value("${lnp.lnd.macaroonPath}") String macaroonPath,
            @Value("${lnp.config.workingDirectory}") String walletDirectory
    ) {
        this.host = host;
        this.port = port;
        this.certificatePath = certificatePath;
        this.macaroonPath = macaroonPath;
        this.walletDirectory = USER_HOME + walletDirectory;
    }

    @Bean
    SynchronousLndAPI synchronousLndAPI() throws ClientSideException, SSLException {
        return new SynchronousLndAPI(host,
                port,
                new File(walletDirectory, certificatePath),
                new File(walletDirectory, macaroonPath));
    }

    @Bean
    AsynchronousLndAPI asynchronousLndAPI(PaymentFacade paymentFacade) throws StatusException, SSLException, ValidationException {
        AsynchronousLndAPI api = new AsynchronousLndAPI(host,
                port,
                new File(walletDirectory, certificatePath),
                new File(walletDirectory, macaroonPath));
        api.subscribeInvoices(new InvoiceSubscription(), new InvoiceObserver(paymentFacade));
        return api;
    }

}
