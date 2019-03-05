package io.restize;

import io.restize.flow.Flow;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 12/10/17
 */
public class TestUtils {
    private static final CloseableHttpClient httpclient =
            HttpClients.custom().build();

    public static void testFlow(Consumer<Flow> flowSetup, ExceptionalRunnable test) {
        Restize restize = Restize.create();
        Flow flow = restize.flow();
        try {
            flowSetup.accept(flow);
            restize.start();
            test.run();
        } catch (Exception e) {
            throw new RuntimeException("Failed ", e);
        } finally {
            restize.stop();
        }
    }
    public static void testFlow(int ioThreads, Consumer<Flow> flowSetup, ExceptionalRunnable test) {
        Restize restize = Restize.create(ioThreads, 5);
        Flow flow = restize.flow();
        try {
            flowSetup.accept(flow);
            restize.start();
            test.run();
        } catch (Exception e) {
            throw new RuntimeException("Failed ", e);
        } finally {
            restize.stop();
        }
    }

    public static void get(String uri, Consumer<HttpResponse> check) {
        HttpGet get = new HttpGet(uri);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            check.accept(response);
        } catch (IOException e) {
            throw new RuntimeException("Error getting " + uri, e);
        }
    }
    public static void getInNewClient(String uri, Consumer<HttpResponse> check) {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        HttpGet get = new HttpGet(uri);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            check.accept(response);
        } catch (IOException e) {
            throw new RuntimeException("Error getting " + uri, e);
        }
    }

    public static void post(String uri, Consumer<HttpResponse> check) {
        HttpPost post = new HttpPost(uri);
        try (CloseableHttpResponse response = httpclient.execute(post)) {
            check.accept(response);
        } catch (IOException e) {
            throw new RuntimeException("Error getting " + uri, e);
        }
    }

    public static String entityAsString(HttpResponse response) {
        try {
            StringBuilder result = new StringBuilder();
            InputStream content = response.getEntity().getContent();
            int c;
            while ((c = content.read()) > 0) {
                result.append((char)c);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to get entity", e);
        }
    }

    public interface ExceptionalRunnable {
        void run() throws Exception;
    }
}
