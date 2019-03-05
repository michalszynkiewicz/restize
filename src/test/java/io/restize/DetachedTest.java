package io.restize;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static io.restize.TestUtils.entityAsString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/11/17
 */
@Disabled
public class DetachedTest {

    @Test
    public void shouldHandleAsync() {
        TestUtils.testFlow(
                flow -> flow.path("foo").handleDetached(this::foo),
                () ->
                        TestUtils.get("http://localhost:8406/foo",
                                response -> assertThat(entityAsString(response)).isEqualTo("foo"))

        );
    }

    @Test
    public void shouldTriggerAsyncAsynchronously() {
        TestUtils.testFlow(
                1,
                flow -> flow.path("foo").handleDetached(new AsyncGetHandler()),
                () -> {
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < 3; i++)
                        TestUtils.getInNewClient("http://localhost:8406/foo",
                                response -> assertThat(entityAsString(response)).isEqualTo("foo")
                        );
                    long end = System.currentTimeMillis();
                    assertThat(end - start)
                            .overridingErrorMessage("Handling 3 requests took %d ms, " +
                                            "it suggests the requests were not handled asynchronously",
                                    end - start)
                            .isLessThan(2000L);
                }

        );
    }

    public static class AsyncGetHandler implements BiConsumer<Request, Response> {

        @Override
        public void accept(Request request, Response response) {
            System.out.println("will sleep");
            try {
                Thread.sleep(40000L); // mstodo bring back 1000L
            } catch (InterruptedException e) {
                e.printStackTrace();  // TODO: Customise this generated block
            }
            System.out.println("woke up, will send response");
            response.setMessage("foo").send();
        }
    }
    private void foo(Request request, Response response) {
        System.out.println("will sleep");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();  // TODO: Customise this generated block
        }
        System.out.println("woke up, will send response");
        response.setMessage("foo").send();
    }
}
