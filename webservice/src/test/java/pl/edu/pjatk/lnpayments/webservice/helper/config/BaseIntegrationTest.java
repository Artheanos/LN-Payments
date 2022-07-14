package pl.edu.pjatk.lnpayments.webservice.helper.config;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public abstract class BaseIntegrationTest {

    protected String getJsonResponse(String path) throws IOException {
        return new String(new ClassPathResource(path).getInputStream().readAllBytes());
    }

}
