package cn.featherfly.jhttp.okhttp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import cn.featherfly.jhttp.Http;
import cn.featherfly.jhttp.HttpClientBuilder;

/**
 * Verify that the builders are discoverable via ServiceLoader.
 *
 * @author zhongj
 */
public class ServiceLoaderSmokeTest {

    @Test
    public void builders() {
        assertTrue(HttpClientBuilder.sync() instanceof cn.featherfly.jhttp.okhttp.HttpSyncClientBuilder);
        assertTrue(HttpClientBuilder.async() instanceof cn.featherfly.jhttp.okhttp.HttpAsyncClientBuilder);
        assertTrue(HttpClientBuilder.rxjava() instanceof cn.featherfly.jhttp.okhttp.HttpRxjavaClientBuilder);

        assertNotNull(HttpClientBuilder.sync().build());
        assertNotNull(HttpClientBuilder.async().build());
        assertNotNull(HttpClientBuilder.rxjava().build());
    }

    @Test
    public void httpFacade() {
        assertEquals(Http.get("http://localhost:8080/hello"), "hello world");
    }
}
