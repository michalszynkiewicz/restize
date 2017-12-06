package io.restize.flow;

import io.restize.RestizeExchange;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class WrongFlow extends Flow {

    public static WrongFlow instance = new WrongFlow();

    @Override
    public void execute(RestizeExchange exchange) {
        exchange.statusCode(503);
        exchange.responseSender().send("Cannot handle request");
    }

    @Override
    protected boolean matches(RestizeExchange exchange) {
        return true;
    }
}
