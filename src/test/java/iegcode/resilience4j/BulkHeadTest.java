package iegcode.resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkHeadTest {

    private AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    public void slow(){
        long value = counter.incrementAndGet();
        log.info("Slow : " + value);
        Thread.sleep(5_000L);
    }

    // constraint tidak bisa diakses ke thread codenya bersamaan lebih dari 25 maka akan error di tolak
    @Test
    void testSemaphore() throws InterruptedException {
        Bulkhead bulkhead = Bulkhead.ofDefaults("ieg");

        for (int i = 0; i < 100; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }
        Thread.sleep(1000);
    }

    // sejumlah prosessor leptop threadpoolbilkhead
    @Test
    void testThreadPool() {
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("ieg");

        for (int i = 0; i < 500; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
    }

    @Test
    void testSemaphoreConfig() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        Bulkhead bulkhead = Bulkhead.of("ieg", config);

        for (int i = 0; i < 4; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolConfig() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(1) // tidak ada antrian
                .build();


        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("ieg", config);

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
        Thread.sleep(10_000L);
    }

    @Test
    void testSemaphoreRegistry() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Bulkhead bulkhead = Bulkhead.of("ieg", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolRegistry() throws InterruptedException {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(1) // tidak ada antrian
                .build();

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        ThreadPoolBulkhead bulkhead = registry.bulkhead("ieg", "config");

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
        Thread.sleep(10_000L);
    }

}
