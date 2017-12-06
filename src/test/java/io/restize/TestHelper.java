package io.restize;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.Invocation;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/6/17
 */
public class TestHelper {

    private static final ResteasyClient client;

    static {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder = clientBuilder.connectionPoolSize(20);
        client = clientBuilder.build();
    }

    public static Invocation.Builder createClient(String uri) {
        return client.target(uri)
                .request();
    }

}
