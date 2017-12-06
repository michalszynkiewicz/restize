package io.restize.flow;

import io.restize.RestizeExchange;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class RootFlow extends Flow {
    @Override
    protected boolean matches(RestizeExchange exchange) {
        return true;
    }
}
