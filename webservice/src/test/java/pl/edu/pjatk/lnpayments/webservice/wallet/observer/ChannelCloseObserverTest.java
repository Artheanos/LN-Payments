package pl.edu.pjatk.lnpayments.webservice.wallet.observer;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.message.ChannelCloseUpdate;
import org.lightningj.lnd.wrapper.message.CloseStatusUpdate;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class ChannelCloseObserverTest {

    private ListAppender<ILoggingEvent> logAppender;

    private final ChannelCloseObserver channelCloseObserver = new ChannelCloseObserver();

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(ChannelCloseObserver.class)).addAppender(logAppender);
    }

    @Test
    void shouldLogWhenStatusUpdateIsSuccess() {
        ChannelCloseUpdate update = new ChannelCloseUpdate();
        update.setSuccess(true);
        CloseStatusUpdate closeUpdate = new CloseStatusUpdate();
        closeUpdate.setChanClose(update);

        channelCloseObserver.onNext(closeUpdate);

        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Channel close success: ", Level.INFO));
    }

    @Test
    void shouldNotLogAnythingWhenNoSuccess() {
        ChannelCloseUpdate update = new ChannelCloseUpdate();
        update.setSuccess(false);
        CloseStatusUpdate closeUpdate = new CloseStatusUpdate();
        closeUpdate.setChanClose(update);

        channelCloseObserver.onNext(closeUpdate);

        assertThat(logAppender.list).isEmpty();
    }

    @Test
    void shouldLogMessageOnSuccess() {
        channelCloseObserver.onCompleted();
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Channel close request completed", Level.INFO));
    }

    @Test
    void shouldLogAndThrowExceptionOnError() {
        Throwable throwable = new RuntimeException();
        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> channelCloseObserver.onError(throwable))
                .withMessage("Error closing channel");
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Error while closing channel: null", Level.ERROR));
    }
}
