package io.restize;

import io.restize.flow.Flow;
import io.restize.flow.RootFlow;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/5/17
 */
public class Restize {
    public static Restize create() {
        return new Restize();
    }

    private Undertow server;

    public Restize() {
        flow = new RootFlow();
    }

    private final Flow flow;

    public void start() {
        start(8406);
    }

    public void start(int port) {
        long start = System.currentTimeMillis();
        server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0") // TODO customize the host
                .setHandler(this::httpHandler).build();
        server.start();
        // todo some logging framework
        System.out.printf("Start-up time: %d [ms]\n", System.currentTimeMillis() - start);
    }

    private void httpHandler(HttpServerExchange exchange) {
        RestizeExchange restizeExchange = new RestizeExchange(exchange, exchange.getRelativePath());
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
