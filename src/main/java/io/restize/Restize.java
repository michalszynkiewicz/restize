package io.restize;

import io.restize.async.ForkJoinThreadFactory;
import io.restize.flow.Flow;
import io.restize.flow.RootFlow;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/5/17
 */
public class Restize {
    public static Restize create() {
        return new Restize(Integer.max(Runtime.getRuntime().availableProcessors(), 2), 20);
    }

    public static Restize create(int ioThreads, int forkJoinPoolSize) {
        return new Restize(ioThreads, forkJoinPoolSize);
    }

    private Undertow server;
    private final Flow flow;

    private final int ioThreads;
    private final Executor asyncTasksPool;

    public Restize(int ioThreads, int forkJoinPoolSize) {
        flow = new RootFlow();
        this.ioThreads = ioThreads;
        this.asyncTasksPool = Executors.newFixedThreadPool(10);
//                n

//        ew ForkJoinPool(
//                forkJoinPoolSize,
//                new ForkJoinThreadFactory("async-tasks-"),
//                null,
//                true
//        );

    }


    public void start() {
        start(8406);
    }

    public void start(int port) {
        long start = System.currentTimeMillis();
        server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0") // TODO customize the host
                .setWorkerThreads(4)             // todo expose customization options
//                .setWorkerThreads(workerThreads)             // todo expose customization options
                .setIoThreads(Math.max(ioThreads, 1))
                .setHandler(this::httpHandler).build();
        server.start();
        // todo some logging framework
        System.out.printf("Start-up time: %d [ms]\n", System.currentTimeMillis() - start);
    }

    private void httpHandler(HttpServerExchange exchange) {
        RestizeExchange restizeExchange =
                new RestizeExchange(exchange, exchange.getRelativePath(), asyncTasksPool);
        flow.execute(restizeExchange);
    }

    public Flow flow() {
        return flow;
    }

    public boolean stop() {
        if (server != null) {
            server.stop();
            return true;
        }
        return false;
    }
}
