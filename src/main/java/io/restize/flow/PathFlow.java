package io.restize.flow;

import io.restize.RestizeExchange;
import io.undertow.server.HttpServerExchange;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class PathFlow extends Flow {
    private final String path;

    public PathFlow(String path) {
        this.path = addForwardAndRemoveTrailingSlash(path);
    }

    private String addForwardAndRemoveTrailingSlash(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public <T> void execute(RestizeExchange exchange) {
        exchange.consumePath(path);
        super.execute(exchange);
    }

    @Override
    protected boolean matches(RestizeExchange exchange) {
        return exchange.pathStartsWith(path);
    }
}
