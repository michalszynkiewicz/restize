package io.restize;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class RestizeExchange {
    public final HttpServerExchange exchange;
    public char path[];
    private int pathPosition = 0;
    private Map<String, String> pathParams = new HashMap<>();

    public RestizeExchange(HttpServerExchange exchange,
                           String path) {
        this.exchange = exchange;
        this.path = path.toCharArray();
    }

    public RestizeExchange statusCode(int statusCode) {
        this.exchange.setStatusCode(statusCode);
        return this;
    }

    public Sender responseSender() {
        return exchange.getResponseSender();
    }

    public HeaderMap responseHeaders() {
        return exchange.getResponseHeaders();
    }

    public String path() {
        return new String(path);
    }

    public HeaderMap requestHeaders() {
        return exchange.getRequestHeaders();
    }

    public HttpString method() {
        return exchange.getRequestMethod();
    }

    /*
     * this method assumes the `matches` method invoked before already checked if the string to consume is
     * at the begging of the current path
     */
    public RestizeExchange consumePath(String toConsume) {
        pathPosition += toConsume.length();
        if (hasPathLeft() && path[pathPosition] == '/') {
            pathPosition++;
        }
        return this;
    }

    public boolean pathStartsWith(String toConsume) {
        if (path.length - pathPosition < toConsume.length()) {
            return false;
        }
        int i = 0;
        for (; i < toConsume.length(); i++) {
            if (path[i + pathPosition] != toConsume.charAt(i)) {
                return false;
            }
        }
        return i == path.length || path[i++] == '/';
    }

    public String pathLeft() {
        return new String(path, pathPosition, path.length - pathPosition);
    }

    public boolean hasPathLeft() {
        return path.length > pathPosition;
    }

    public String consumeUntil(char character) {
        StringBuilder result = new StringBuilder();
        for (; pathPosition < path.length; pathPosition++) {
            if (path[pathPosition] == character) {
                pathPosition++;
                break;
            }
            result.append(path[pathPosition]);
        }
        return result.toString();
    }

    public void pathParam(String paramName, String value) {
        pathParams.put(paramName, value);
    }

    public String pathParam(String name) {
        return pathParams.get(name);
    }

    public String queryParam(String paramName) {
        Deque<String> values = exchange.getQueryParameters().get(paramName);

        if (values.size() > 1) {
            // mstodo bettere xceptions!
            throw new IllegalStateException("Too many param values. Expected 1, got " + values.size());
        }
        return values.getFirst();
    }

    public Collection<String> queryParams(String paramName) {
        return exchange.getQueryParameters().get(paramName);
    }
}
