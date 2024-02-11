package iegcode.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RetryConfigTest {

    String hello(){
        log.info("Call Hello");
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void retryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5) // output 5 buah info main
                .waitDuration(Duration.ofSeconds(2)) // durations
//                .ignoreExceptions(IllegalArgumentException.class)
                .retryExceptions(IllegalArgumentException.class)
                .build();

        Retry retry = Retry.of("Ieg", config);

        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
        supplier.get();
    }
}
