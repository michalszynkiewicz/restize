package io.restize.flow;

import io.undertow.util.HttpString;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class Flows {
    public static Flow get() {
        return new MethodFlow(new HttpString("GET"));
    }

    public static Flow header(String name, String value) {
        return new HeaderFlow(name, value);
    }

    public static Flow path(String path) {
        return new PathFlow(path);
    }

    public static Flow param(String paramName) {
        return new ParamFlow(paramName);
    }

    public static Flow post() {
        return new MethodFlow(new HttpString("POST"));
    }

    public static Flow put() {
        return new MethodFlow(new HttpString("PUT"));
    }

    public static Flow delete() {
        return new MethodFlow(new HttpString("DELETE"));
    }
}
