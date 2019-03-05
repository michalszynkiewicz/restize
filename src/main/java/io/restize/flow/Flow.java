package io.restize.flow;

import io.restize.Request;
import io.restize.Response;
import io.restize.RestizeExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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

    public Flow post() {
        return subFlow(Flows.post());
    }

    public Flow put() {
        return subFlow(Flows.put());
    }

    public Flow delete() {
        return subFlow(Flows.delete());
    }

    private Flow subFlow(Flow subFlow) {
        children.add(subFlow);
        subFlow.parent = this;
        return subFlow;
    }

    public <Input, Output> Flow handle(Class<Input> entityClass, Function<Input, Output> handleFunction) {
        return subFlow(new ExecuteFlow<>(request -> handleFunction.apply(request.parseTo(entityClass))));
    }

    public <T> Flow handle(Function<Request, T> handle) {
        return subFlow(new ExecuteFlow<>(handle));
    }

    public <T> Flow handle(Supplier<T> handle) {
        return subFlow(new ExecuteFlow<T>(request -> handle.get()));
    }

    public Flow handleDetached(BiConsumer<Request, Response> handler) {
        return subFlow(new DetachedExecuteFlow(handler));
    }

    public <T> void execute(RestizeExchange exchange) {
        Flow child = children.stream()
                .filter(f -> f.matches(exchange))
                .findFirst()
                .orElse(WrongFlow.instance);
        child.execute(exchange);
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
