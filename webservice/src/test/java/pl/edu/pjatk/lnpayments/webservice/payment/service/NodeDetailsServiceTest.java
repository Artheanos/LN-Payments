package pl.edu.pjatk.lnpayments.webservice.payment.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.GetInfoResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import pl.edu.pjatk.lnpayments.webservice.common.service.PropertyService;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeDetailsServiceTest {

    @Mock
    private SynchronousLndAPI synchronousLndApi;

    @Mock
    private PropertyService propertyService;

    @Mock
    private RestTemplate restTemplate;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(NodeDetailsService.class)).addAppender(logAppender);
    }

    @Test
    @SneakyThrows
    void shouldReturnNodeUrlObtainedFromService() {
        String nodePublicKey = "2137";
        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setIdentityPubkey(nodePublicKey);
        when(propertyService.getServerIpAddress()).thenReturn("");
        when(synchronousLndApi.getInfo()).thenReturn(getInfoResponse);
        when(restTemplate.getForObject(anyString(), any())).thenReturn("1.2.3.4");
        NodeDetailsService nodeDetailsService =
                new NodeDetailsService(synchronousLndApi, propertyService, restTemplate, "");

        String nodeUrl = nodeDetailsService.getNodeUrl();

        assertThat(nodeUrl).isEqualTo("2137@1.2.3.4:9735");
    }

    @Test
    @SneakyThrows
    void shouldThrowAndLogExceptionWhenSomethingGoesWrong() {
        when(synchronousLndApi.getInfo()).thenThrow(ValidationException.class);

        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> new NodeDetailsService(synchronousLndApi, propertyService, restTemplate, ""))
                .withMessageStartingWith("Failed to obtain node url")
                .withCauseExactlyInstanceOf(ValidationException.class);
        Assertions.assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to get node address from LND", Level.ERROR));
    }

    @Test
    @SneakyThrows
    void shouldTakeIpFromPropertiesWhenAvailable() {
        String nodePublicKey = "2137";
        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setIdentityPubkey(nodePublicKey);
        when(propertyService.getServerIpAddress()).thenReturn("2.1.3.7");
        when(synchronousLndApi.getInfo()).thenReturn(getInfoResponse);
        NodeDetailsService nodeDetailsService =
                new NodeDetailsService(synchronousLndApi, propertyService, restTemplate, "");

        String nodeUrl = nodeDetailsService.getNodeUrl();

        assertThat(nodeUrl).isEqualTo("2137@2.1.3.7:9735");
        verify(restTemplate, never()).getForObject(anyString(), any());
    }
}
