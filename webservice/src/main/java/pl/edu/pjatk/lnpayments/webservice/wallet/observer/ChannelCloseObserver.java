package pl.edu.pjatk.lnpayments.webservice.wallet.observer;

import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.message.CloseStatusUpdate;
import org.springframework.stereotype.Component;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import java.util.HexFormat;

@Slf4j
@Component
public class ChannelCloseObserver implements StreamObserver<CloseStatusUpdate> {

    @Override
    @SneakyThrows
    public void onNext(CloseStatusUpdate value) {
        if (value.getChanClose().getSuccess()) {
            log.info("Channel close success: " + HexFormat.of().formatHex(value.getChanClose().getClosingTxid()));
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error while closing channel: " + t.getMessage());
        throw new LightningException("Error closing channel", t);
    }

    @Override
    public void onCompleted() {
        log.info("Channel close request completed");
    }
}
