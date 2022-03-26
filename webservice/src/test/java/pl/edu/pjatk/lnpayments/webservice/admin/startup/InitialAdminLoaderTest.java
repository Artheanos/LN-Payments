package pl.edu.pjatk.lnpayments.webservice.admin.startup;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.admin.resource.dto.AdminRequest;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;

import javax.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_EMAIL;
import static pl.edu.pjatk.lnpayments.webservice.common.Constants.ROOT_USER_PASSWORD;

@ExtendWith(MockitoExtension.class)
class InitialAdminLoaderTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private InitialAdminLoader initialAdminLoader;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(InitialAdminLoader.class)).addAppender(logAppender);
    }

    @Test
    void shouldInvokeServiceAndLogInfo() {
        initialAdminLoader.run(null);

        ArgumentCaptor<AdminRequest> captor = ArgumentCaptor.forClass(AdminRequest.class);
        verify(adminService).createAdmin(captor.capture());
        AdminRequest request = captor.getValue();
        assertThat(request.getEmail()).isEqualTo(ROOT_USER_EMAIL);
        assertThat(request.getPassword()).isEqualTo(ROOT_USER_PASSWORD);
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Initial user has been added with email {} and password {}", Level.INFO));
    }

    @Test
    void shouldLogWarningWhenUserAlreadyExists() {
        doThrow(ValidationException.class).when(adminService).createAdmin(any());

        initialAdminLoader.run(null);

        verify(adminService).createAdmin(any());
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Unable to create initial admin user: null", Level.WARN));
    }
}
