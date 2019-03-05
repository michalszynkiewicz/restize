package io.restize.flow;

import io.restize.Request;
import io.restize.RestizeExchange;

import java.util.function.Function;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/11/17
 */
public abstract class TerminalFlow extends Flow {
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


    private <V> V notAllowed() {
        throw new IllegalStateException("handle() is the final element of the flow");
    }
}
