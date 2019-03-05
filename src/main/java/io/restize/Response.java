package io.restize;

import io.undertow.util.HttpString;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/5/17
 */
public class Response {

    private final RestizeExchange exchange;
    private int statusCode = 200;
    private String message = "";

    public Response(RestizeExchange exchange) {
        this.exchange = exchange;
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Response setHeader(String name, String value) {
        this.exchange.responseHeaders().put(HttpString.tryFromString(name), value);
        return this;
    }

    public void send() {
        exchange.undertowExchange.setStatusCode(statusCode);
        exchange.undertowExchange.getResponseSender().send(message);
    }

    public static Response fromExchange(RestizeExchange exchange) {
        return new Response(exchange);
    }
}
