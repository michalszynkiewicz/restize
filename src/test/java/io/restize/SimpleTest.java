package io.restize;

import io.restize.flow.Flow;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.function.Consumer;

import static io.restize.TestHelper.createClient;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/5/17
 */
public class SimpleTest {

    public static final int NUMBER = 1132;
    public static final String FOO_PARAM = "fooParam";

    @Test
    public void shouldExposeGet() {
        testFlow(flow -> flow.get().handle(this::getNumber),
                () -> {
                    String uri = "http://localhost:8406";
                    Response response = createClient(uri).get();
                    assertThat(response.getStatus()).isEqualTo(200);
                    assertThat(response.readEntity(Integer.class)).isEqualTo(NUMBER);
                }
        );
    }

    @Test
    public void shouldExposeGetWithPath() {
        testFlow(flow -> flow.get().path("/foo").handle(this::getNumber),
                () -> {
                    Response response = createClient("http://localhost:8406/foo").get();
                    assertThat(response.getStatus()).isEqualTo(200);
                    assertThat(response.readEntity(Integer.class)).isEqualTo(NUMBER);
                }
        );
    }

    @Test
    public void shouldExposePathWithGet() {
        testFlow(flow -> flow.path("/foo").get().handle(this::getNumber),
                () -> {
                    Response response = createClient("http://localhost:8406/foo").get();
                    assertThat(response.getStatus()).isEqualTo(200);
                    assertThat(response.readEntity(Integer.class)).isEqualTo(NUMBER);
                }
        );
    }

    @Test
    public void shouldExposePathWithParam() {
        testFlow(flow -> flow.path("/foo").param(FOO_PARAM).handle(this::withParam),
                () -> {
                    Response response = createClient("http://localhost:8406/foo/world").get();
                    assertThat(response.getStatus()).isEqualTo(200);
                    assertThat(response.readEntity(String.class)).isEqualTo("Hello, world");
                }
        );
    }

    @Test
    public void shouldParseQueryParams() {
        testFlow(
                flow -> flow.path("path").get().handle(this::handleRequest),
                () -> {
                    Response response = createClient("http://localhost:8406/path?p1=v1&p2=v2").get();
                    assertThat(response.getStatus()).isEqualTo(200);
                    assertThat(response.readEntity(String.class)).isEqualTo("param1:v1,param2:v2");
                }
        );
    }

    private String handleRequest(Request request) {
        return String.format("param1:%s,param2:%s",
                request.queryParam("p1"),
                request.queryParam("p2")
        );
    }

    private void testFlow(Consumer<Flow> flowSetup, Runnable test) {
        Restize restize = Restize.create();
        Flow flow = restize.flow();
        try {
            flowSetup.accept(flow);
            restize.start();
            test.run();
        } finally {
            restize.stop();
        }

    }

    private String withParam(Request request) {
        String paramValue = request.pathParam(FOO_PARAM);
        return "Hello, " + paramValue;
    }

    private Integer getNumber() {
        return NUMBER;
    }
}
