package iegcode.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterTest {

    private final AtomicLong counter = new AtomicLong(0L);

    @Test
    void testRateLimiter() {
        RateLimiter rateLimiter = RateLimiter.ofDefaults("IEG");

        for (int i = 0; i < 100; i++) {
            Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
                long result = counter.incrementAndGet();
                log.info("Result: {}", result);
            });
            runnable.run();
        }

    }
}
