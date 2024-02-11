package iegcode.resilience4j;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
}
