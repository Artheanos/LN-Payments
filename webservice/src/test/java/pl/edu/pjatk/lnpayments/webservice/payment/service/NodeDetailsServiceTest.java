package pl.edu.pjatk.lnpayments.webservice.payment.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.GetInfoResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@WireMockTest(httpPort = 8079)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NodeDetailsServiceTest {

    @Mock
    private SynchronousLndAPI synchronousLndApi;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    @SneakyThrows
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(NodeDetailsService.class)).addAppender(logAppender);
        stubFor(get("/ip").willReturn(ok("1.2.3.4")));
    }

    @Test
    @SneakyThrows
    void shouldReturnNodeUrl() {
        String nodePublicKey = "2137";
        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setIdentityPubkey(nodePublicKey);
        when(synchronousLndApi.getInfo()).thenReturn(getInfoResponse);
        NodeDetailsService nodeDetailsService =
                new NodeDetailsService(synchronousLndApi, "http://localhost:8079/ip");

        String nodeUrl = nodeDetailsService.getNodeUrl();

        assertThat(nodeUrl).isEqualTo("2137@1.2.3.4:9735");
    }

    @Test
    @SneakyThrows
    void shouldThrowAndLogExceptionWhenSomethingGoesWrong() {
        when(synchronousLndApi.getInfo()).thenThrow(ValidationException.class);

        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> new NodeDetailsService(synchronousLndApi, ""))
                .withMessageStartingWith("Failed to obtain node url")
                .withCauseExactlyInstanceOf(ValidationException.class);
        Assertions.assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to get node address from LND", Level.ERROR));
    }
}
