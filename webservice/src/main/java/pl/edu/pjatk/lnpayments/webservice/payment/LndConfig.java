package pl.edu.pjatk.lnpayments.webservice.payment;

import org.lightningj.lnd.wrapper.ClientSideException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.File;

@Configuration
class LndConfig {

    private final String host;
    private final int port;
    private final String certificatePath;
    private final String macaroonPath;

    @Autowired
    LndConfig(
            @Value("${lnp.lnd.host}") String host,
            @Value("${lnp.lnd.port}") int port,
            @Value("${lnp.lnd.certificatePath}") String certificatePath,
            @Value("${lnp.lnd.macaroonPath}") String macaroonPath) {
        this.host = host;
        this.port = port;
        this.certificatePath = certificatePath;
        this.macaroonPath = macaroonPath;
    }

    @Bean
    SynchronousLndAPI synchronousLndAPI() throws ClientSideException, SSLException {
        return new SynchronousLndAPI(host, port, new File(certificatePath), new File(macaroonPath));
    }

}
