package iegcode.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryRegistryTest {

    void callMe(){
        log.info("Try Call Me");
        throw new IllegalArgumentException("ups error");
    }

    @Test
    void testRegistry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();

        Retry retry1 = registry.retry("ieg");
        Retry retry2 = registry.retry("ieg");

        Assertions.assertSame(retry1, retry2);
    }
    @Test
    void testRegistryConfig() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(1))
                .build();

        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Retry retry1 = registry.retry("ieg", "config");
        Retry retry2 = registry.retry("ieg", "config");

        Assertions.assertSame(retry1, retry2);

        Runnable runnable = Retry.decorateRunnable(retry1, () -> callMe());
        runnable.run();
    }
}
