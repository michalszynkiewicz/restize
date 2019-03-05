package io.restize.flow;

import io.restize.RestizeExchange;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class ParamFlow extends Flow {
    private final String paramName;

    public ParamFlow(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public <T> void execute(RestizeExchange exchange) {
        String value = exchange.consumePathUntil('/');
        exchange.pathParam(paramName, value);
        super.execute(exchange);
    }

    @Override
    protected boolean matches(RestizeExchange exchange) {
        return exchange.hasPathLeft();
    }
}
