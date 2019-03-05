package io.restize;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class RestizeExchange {
    public final HttpServerExchange undertowExchange;
    public final char path[];

    public final Executor asyncPool;
    private int pathPosition = 0;
    private Map<String, String> pathParams = new HashMap<>();
    private byte[] content;

    public RestizeExchange(HttpServerExchange undertowExchange,
                           String path,
                           Executor asyncPool) {
        this.undertowExchange = undertowExchange;
        this.path = path.toCharArray();
        this.asyncPool = asyncPool;
    }

    public RestizeExchange statusCode(int statusCode) {
        this.undertowExchange.setStatusCode(statusCode);
        return this;
    }

    public Sender responseSender() {
        return undertowExchange.getResponseSender();
    }

    public HeaderMap responseHeaders() {
        return undertowExchange.getResponseHeaders();
    }

    public String path() {
        return new String(path);
    }

    public HeaderMap requestHeaders() {
        return undertowExchange.getRequestHeaders();
    }

    public HttpString method() {
        return undertowExchange.getRequestMethod();
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

    public String consumePathUntil(char character) {
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
        Deque<String> values = undertowExchange.getQueryParameters().get(paramName);

        if (values.size() > 1) {
            // mstodo bettere xceptions!
            throw new IllegalStateException("Too many param values. Expected 1, got " + values.size());
        }
        return values.getFirst();
    }

    public Collection<String> queryParams(String paramName) {
        return undertowExchange.getQueryParameters().get(paramName);
    }

    public void detach() {
        undertowExchange.startBlocking();
        InputStream inputStream = undertowExchange.getInputStream();
        content = readToBytes(inputStream);
    }

    private byte[] readToBytes(InputStream inputStream) {
        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
        int read;
        byte buffer[] = new byte[4096];
        try {
            while ((read = inputStream.read(buffer)) > -1) {
                contentStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException("oops, error reading stream", e);
        }
        return contentStream.toByteArray();
    }

    public InputStream getContent() {
        return content == null
                ? undertowExchange.getInputStream()
                : new ByteArrayInputStream(content);
    }
}
