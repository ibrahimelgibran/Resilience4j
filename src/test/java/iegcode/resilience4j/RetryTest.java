package iegcode.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {

    void callMe(){
        log.info("Try Call Me");
        throw new IllegalArgumentException("ups error");
    }

    @Test
    void createNewRetry() {
        Retry retry = Retry.ofDefaults("ieg");

        Runnable runnable = Retry.decorateRunnable(retry, () -> callMe());
            runnable.run();
    }

    String hello(){
        log.info("Call say hello");
        throw new IllegalArgumentException("ups error say hello");
    }

    @Test
    void createRetrySupplier() {
        Retry retry = Retry.ofDefaults("ieg");

        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
        supplier.get();
    }
}
