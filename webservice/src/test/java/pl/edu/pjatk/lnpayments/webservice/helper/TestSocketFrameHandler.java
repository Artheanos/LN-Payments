package pl.edu.pjatk.lnpayments.webservice.helper;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestSocketFrameHandler<T> implements StompFrameHandler {

    private final Class<T> type;
    private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

    private TestSocketFrameHandler(Class<T> type) {
        this.type = type;
    }

    public static <T> TestSocketFrameHandler<T> of(Class<T> type) {
        return new TestSocketFrameHandler<>(type);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleFrame(StompHeaders headers, Object payload) {
        completableFuture.complete((T) payload);
    }

    public T getResult() throws ExecutionException, InterruptedException, TimeoutException {
        return completableFuture.get(5, SECONDS);
    }
}
