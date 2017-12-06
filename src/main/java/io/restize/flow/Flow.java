package io.restize.flow;

import io.restize.Request;
import io.restize.RestizeExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public abstract class Flow {

    private List<Flow> children = new ArrayList<>(); // TODO speed this up
    private Flow parent;

    public Flow get() {
        return subFlow(Flows.get());
    }

    public Flow header(String name, String value) {
        return subFlow(Flows.header(name, value));
    }

    public Flow path(String path) {
        return subFlow(Flows.path(path));
    }

    private Flow subFlow(Flow subFlow) {
        children.add(subFlow);
        subFlow.parent = this;
        return subFlow;
    }

    public <T> Flow handle(Function<Request, T> handle) {
        return subFlow(new ExecuteFlow<T>(handle));
//                (path, exchange -> {
//            Request request = Request.fromExchange(exchange);
//            handle.apply(request);
//        });
    }
    public <T> Flow handle(Supplier<T> handle) {
        return subFlow(new ExecuteFlow<T>(request -> handle.get()));
    }

    public <T> void execute(RestizeExchange exchange) {
        children.stream()
                .filter(f -> f.matches(exchange))
                .findFirst()
                .orElse(WrongFlow.instance)
                .execute(exchange);
    }

    protected abstract boolean matches(RestizeExchange exchange);

    public Flow param(String fooParam) {
        return subFlow(Flows.param(fooParam));
    }

    public void either(Flow... children) {
        Stream.of(children)
                .map(Flow::getRoot)
                .filter(f -> f.getRoot() != getRoot())
                .forEach(this::subFlow);
    }

    public Flow getRoot() { // mstodo revoke access
        Flow root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root;
    }
}
