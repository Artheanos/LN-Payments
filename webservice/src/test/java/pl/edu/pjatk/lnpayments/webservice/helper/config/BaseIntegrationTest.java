package pl.edu.pjatk.lnpayments.webservice.helper.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;

public abstract class BaseIntegrationTest {

    protected static WireMockServer mockHttpServer = new WireMockServer(8079);

    @BeforeAll
    static void setup() {
        mockHttpServer.stubFor(get("/ip").willReturn(ok("1.2.3.4")));
        mockHttpServer.start();
    }

    @AfterAll
    static void teardown() {
        mockHttpServer.stop();
    }

    protected String getJsonResponse(String path) throws IOException {
        return new String(new ClassPathResource(path).getInputStream().readAllBytes());
    }

}
