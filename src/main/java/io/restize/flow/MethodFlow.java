package io.restize.flow;

import io.restize.RestizeExchange;
import io.undertow.util.HttpString; /**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class MethodFlow extends Flow {

    private final HttpString method;

    public MethodFlow(HttpString method) {
        this.method = method;
    }


    @Override
    protected boolean matches(RestizeExchange exchange) {
        return exchange.method().equals(method);
    }
}
