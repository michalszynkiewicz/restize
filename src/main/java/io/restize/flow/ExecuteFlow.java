package io.restize.flow;

import io.restize.Request;
import io.restize.RestizeExchange;
import io.undertow.util.HttpString;

import java.util.function.Function;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class ExecuteFlow<T> extends TerminalFlow {

    private final Function<Request, T> handle;

    public ExecuteFlow(Function<Request, T> handle) {
        this.handle = handle;
    }


    @Override
    public void execute(RestizeExchange exchange) {
        Request request = Request.fromExchange(exchange);
        T result = handle.apply(request);
        exchange.statusCode(200);
        exchange.responseHeaders().add(new HttpString("Content-Type"), "application/json");
        //  mstodo!!! :
        exchange.responseSender().send(result.toString());
        exchange.responseSender().close();
    }

}
