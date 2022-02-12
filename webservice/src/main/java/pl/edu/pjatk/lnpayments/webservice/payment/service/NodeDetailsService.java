package pl.edu.pjatk.lnpayments.webservice.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
@Service
public class NodeDetailsService {

    private final SynchronousLndAPI synchronousLndAPI;
    private final String nodeUrl;
    private final String ipServiceUrl;

    @Autowired
    NodeDetailsService(SynchronousLndAPI synchronousLndAPI, @Value("${lnp.misc.ipRetrieverUrl}") String ipServiceUrl) {
        this.synchronousLndAPI = synchronousLndAPI;
        this.ipServiceUrl = ipServiceUrl;
        nodeUrl = buildNodeUrl();
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    private String buildNodeUrl() {
        try {
            String nodeKey = synchronousLndAPI.getInfo().getIdentityPubkey();
            String ip = retrievePublicIp();
            return String.format("%s@%s:9735", nodeKey, ip);
        } catch (StatusException | ValidationException | IOException e) {
            log.error("Unable to get node address from LND", e);
            throw new LightningException("Failed to obtain node url", e);
        }
    }

    private String retrievePublicIp() throws IOException {
        URL url = new URL(ipServiceUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return reader.readLine();
    }
}
