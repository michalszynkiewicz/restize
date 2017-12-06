package io.restize.flow;

import io.restize.RestizeExchange;
import io.undertow.server.HttpServerExchange;

import java.util.Optional;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class HeaderFlow extends Flow {
    private final String name;
    private final Optional<String> value;

    public HeaderFlow(String name, String value) {
        this.name = name;
        this.value = Optional.ofNullable(value);
    }

    @Override
    protected boolean matches(RestizeExchange exchange) {
        throw new IllegalStateException("Unimplemented!");
    }
}
