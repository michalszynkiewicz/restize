package io.restize;

import io.undertow.util.HeaderMap;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/5/17
 */
public class Request {

    private final RestizeExchange exchange;

    public Request(RestizeExchange exchange) {
        this.exchange = exchange;
    }

    public String path() {
        return exchange.path();
    }

    // TODO: wrap in a simpler structure
    public HeaderMap headers() {
        return exchange.requestHeaders();
    }

    public String pathParam(String name) {
        return exchange.pathParam(name);
    }

    public static Request fromExchange(RestizeExchange exchange) {
        return new Request(exchange);
    }

    public String queryParam(String paramName) {
        return exchange.queryParam(paramName);
    }
}
