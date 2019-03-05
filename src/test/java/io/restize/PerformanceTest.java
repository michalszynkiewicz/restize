package io.restize;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restize.TestUtils.testFlow;
import static org.assertj.core.api.Assertions.fail;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/10/17
 */
@Tag("perf")
public class PerformanceTest {
    @Test
    public void shouldHandle10KSequentialIn1s() {
        testFlow(
                flow -> flow.get().handle(() -> ""),
                () -> {
                    // warm-up
                    for (int i = 0; i < 20_000; i++) simpleGet();

                    long startTime = System.currentTimeMillis();
                    for (int i = 0; i < 10_000; i++) {
                        simpleGet();
                    }

                    long processingTime = System.currentTimeMillis() - startTime;
                    System.out.println("Processing time: " + processingTime + "[ms]");
                    if (processingTime > 1_000L) {
                        fail("processing took too long. Expecting less than a second, actual: " + processingTime + " ms");
                    }
                }
        );
    }

    // TODO: @ignore this test or move to some cathegory
    // TODO: it will fail on older hardware
    @Test
    public void shouldHandle500KParallelIn15s() {
        long maxTime = 15_000L;
        int requests = 500_000;
        int nThreads = 3;
        testFlow(1,
                flow -> flow.get().handle(() -> ""),
                () -> {
                    // warm-up
                    for (int i = 0; i < 20_000; i++) {
                        simpleGet();
                    }

                    long processingTime = inNThreadsDo(() -> {
                        for (int i = 0; i < requests / nThreads; i++) {
                            simpleGet();
                        }
                        return null;
                    }, nThreads);

                    System.out.println("Processing time: " + processingTime + "[ms]");

                    if (processingTime > maxTime) {
                        fail("processing took too long. Expected less than" + maxTime + " ms, actual: " + processingTime + " ms");
                    }
                }
        );
    }

    private long inNThreadsDo(Callable<Void> test, int nThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        System.out.println("Warm-up finished");
        System.out.flush();
        Collection<Callable<Void>> tests = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {       // warm-up
            tests.add(test);
        }
        long startTime = System.currentTimeMillis();
        List<Future<Void>> futures = executorService.invokeAll(tests);
        futures.forEach(f -> {
            try {
                f.get();
            } catch (Exception any) {
                throw new RuntimeException("Failed ", any);
            }
        });
        return System.currentTimeMillis() - startTime;
    }

    private void simpleGet() {
        TestUtils.get("http://localhost:8406",
                response -> {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        fail("Invalid status. Expected 200, got: " + statusCode);
                    }
                });
    }
}
