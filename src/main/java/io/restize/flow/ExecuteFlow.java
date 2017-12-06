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
public class ExecuteFlow<T> extends Flow {

    private final Function<Request, T> handle;

    public ExecuteFlow(Function<Request, T> handle) {
        this.handle = handle;
    }

    @Override
    public Flow get() {
        return notAllowed();
    }

    @Override
    public Flow header(String name, String value) {
        return notAllowed();
    }

    @Override
    public Flow path(String path) {
        return notAllowed();
    }

    @Override
    public <V> Flow handle(Function<Request, V> handle) {
        return notAllowed();
    }

    @Override
    protected boolean matches(RestizeExchange exchange) {
        return true;
    }

    @Override
    public void execute(RestizeExchange exchange) {
        Request request = Request.fromExchange(exchange);
        T result = handle.apply(request);
        exchange.statusCode(200);
        exchange.responseHeaders().add(new HttpString("Content-Type"), "application/json");
        //  mstodo!!! :
        exchange.responseSender().send(result.toString());
    }

    private <V> V notAllowed() {
        throw new IllegalStateException("handle() is the final element of the flow");
    }
}
