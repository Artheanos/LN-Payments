package pl.edu.pjatk.lnpayments.webservice.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import java.io.IOException;

@Slf4j
@Service
public class NodeDetailsService {

    private final SynchronousLndAPI synchronousLndAPI;
    private final PropertyService propertyService;
    private final RestTemplate restTemplate;
    private final String nodeUrl;
    private final String ipServiceUrl;

    @Autowired
    NodeDetailsService(SynchronousLndAPI synchronousLndAPI,
                       PropertyService propertyService,
                       RestTemplate restTemplate,
                       @Value("${lnp.misc.ipRetrieverUrl}") String ipServiceUrl) {
        this.synchronousLndAPI = synchronousLndAPI;
        this.propertyService = propertyService;
        this.restTemplate = restTemplate;
        this.ipServiceUrl = ipServiceUrl;
        nodeUrl = buildNodeUrl();
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    private String buildNodeUrl() {
        try {
            String nodeKey = synchronousLndAPI.getInfo().getIdentityPubkey();
            String ip = retrievePublicIp().trim();
            return String.format("%s@%s:9735", nodeKey, ip);
        } catch (StatusException | ValidationException | IOException | NullPointerException e) {
            log.error("Unable to get node address from LND", e);
            throw new LightningException("Failed to obtain node url", e);
        }
    }

    private String retrievePublicIp() throws IOException {
        String serverIpAddress = propertyService.getServerIpAddress();
        if (serverIpAddress.isBlank()) {
            return restTemplate.getForObject(ipServiceUrl, String.class);
        }
        return serverIpAddress;
    }
}
