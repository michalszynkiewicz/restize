package io.restize;

import java.util.function.Supplier;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class ComplicatedTest {
//    @Test mstodo!
//    public void shouldMatchPathsInOrder() {
//        Restize restize = Restize.create();
//        try {
//            restize.flow().get().either(
//                    path("/foos/zoos")
//                            .handle(returnString("FooZoo1")),
//                    path("/foos")
//                            .handle(returnString("Foo1")), // todo consider switch to pathPart to be able to reorder flow parts
//                    path("/boos")
//                            .handle(returnString("Boo1"))
//            );
//            restize.start();
//
//            String foozoo = createClient("http://localhost:8406/foos/zoos").get().readEntity(String.class);
//            assertThat(foozoo).isEqualTo("FooZoo1");
//            String foo = createClient("http://localhost:8406/foos/sthelse").get().readEntity(String.class);
//            assertThat(foo).isEqualTo("Foo1");
//            String boo = createClient("http://localhost:8406/boos").get().readEntity(String.class);
//            assertThat(boo).isEqualTo("Boo1");
//        } finally {
//            restize.stop();
//        }
//    }

    private Supplier<String> returnString(String fooZoo1) {
        return () -> fooZoo1;
    }
}
