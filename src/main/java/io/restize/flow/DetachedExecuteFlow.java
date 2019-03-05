package io.restize.flow;

import io.restize.Request;
import io.restize.Response;
import io.restize.RestizeExchange;

import java.util.function.BiConsumer;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/11/17
 */
public class DetachedExecuteFlow extends TerminalFlow {
    private final BiConsumer<Request, Response> handler;

    public DetachedExecuteFlow(BiConsumer<Request, Response> handler) {
        this.handler = handler;
    }

    @Override
    public void execute(RestizeExchange exchange) {
        if (exchange.undertowExchange.isInIoThread()) {
            System.out.println("dispatching from thread " + Thread.currentThread());
            exchange.undertowExchange.dispatch(exchange.asyncPool, () -> asyncExecute(exchange));
            return;
        }
    }

    public void asyncExecute(RestizeExchange exchange) {
//        exchange.forkJoinPool.execute(
//                () -> {
        exchange.detach();
        System.out.println("executing in thread " + Thread.currentThread());
        handler.accept(
                Request.fromExchange(exchange),
                Response.fromExchange(exchange)
        );
        System.out.println("finished in thread " + Thread.currentThread());
//                }
//        );
    }



    /*    @Override
    public void execute(RestizeExchange exchange) {
        Request request = Request.fromExchange(exchange);
        T result = handle.apply(request);
        exchange.statusCode(200);
        exchange.responseHeaders().add(new HttpString("Content-Type"), "application/json");
        //  mstodo!!! :
        exchange.responseSender().send(result.toString());
        exchange.responseSender().close();
    }*/
}
