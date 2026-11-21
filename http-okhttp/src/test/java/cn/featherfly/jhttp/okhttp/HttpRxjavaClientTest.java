
package cn.featherfly.jhttp.okhttp;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Console;
import cn.featherfly.common.lang.Str;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpRxjavaClient;
import cn.featherfly.jhttp.MediaType;

/**
 * Test for the rxjava client.
 *
 * @author zhongj
 */
public class HttpRxjavaClientTest {

    final String host = "http://localhost:8080/{0}";

    HttpRxjavaClientImpl http = HttpRxjavaClientImpl.builder().build();

    User user;

    final String OK = "OK";

    @Test
    public void get() throws InterruptedException {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>();
        http.get(Str.format(host, "user")).subscribe(r -> {
            result.set(r);
            latch.countDown();
        }, e -> {
            e.printStackTrace();
            latch.countDown();
        }, latch::countDown);
        latch.await(10, TimeUnit.SECONDS);
        assertEquals(result.get(), json);
    }

    @Test
    public void listener() throws InterruptedException {
        String url = Str.format(host, "user2");

        AtomicLong deserializeCount = new AtomicLong(0);
        AtomicLong serializeCount = new AtomicLong(0);
        HttpListener listener = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                Console.log("onDeserialize: mediaType = {}", mediaType.toString());
                deserializeCount.incrementAndGet();
                System.out.println(new String(responseBody, mediaType.getCharset()));
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                Console.log("onSerialize: mediaType = {}", mediaType.toString());
                System.out.println(new String(serializeBody, mediaType.getCharset()));
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener2 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                System.out.println("onDeserialize2");
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                System.out.println("onSerialize2");
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener3 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                System.out.println("onDeserialize3");
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                System.out.println("onSerialize3");
                serializeCount.incrementAndGet();
            }
        };

        HttpRxjavaClient http = new HttpRxjavaClientBuilder()//
            .listener(listener, listener2, listener3).build();

        http.post(url, user, Result.class).subscribe(result -> {
            System.out.println(result);
            assertEquals(result.getCode(), OK);
            assertEquals(deserializeCount.get(), 3);
            assertEquals(serializeCount.get(), 3);
        });

        http.post(req -> req.removeListener(listener, listener2, listener3).url(url).parameter(user), Result.class)
            .subscribe(result -> {
                System.out.println(result);
                assertEquals(result.getCode(), OK);
                assertEquals(deserializeCount.get(), 0);
                assertEquals(serializeCount.get(), 0);
            });

        Thread.sleep(1000L);
        System.out.println("end");
    }

    @Test
    public void downloadToFile() throws InterruptedException {
        String url = "https://mirrors.cloud.tencent.com/debian/dists/stable/main/binary-amd64/Packages.xz";
        java.io.File file = new java.io.File(System.getProperty("java.io.tmpdir"),
            "easy-httpclient5-rx-" + System.currentTimeMillis());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Long> size = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        http.download(url, file).subscribe(r -> {
            size.set(r);
            latch.countDown();
        }, e -> {
            error.set(e);
            latch.countDown();
        });
        latch.await(60, TimeUnit.SECONDS);
        if (error.get() != null) {
            throw new AssertionError("download failed", error.get());
        }
        assertEquals(file.length(), size.get().longValue());
        // on Windows an open FileOutputStream would prevent deletion
        assertEquals(file.delete(), true, "download output stream was not closed");
    }

    @Test
    public void post() throws InterruptedException {
        User user = new User();
        user.setName("yufei羽飞");
        user.setAge(18);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Result> result = new AtomicReference<>();
        http.post(Str.format(host, "user2"), user, Result.class).subscribe(r -> {
            result.set(r);
            latch.countDown();
        }, e -> {
            e.printStackTrace();
            latch.countDown();
        }, latch::countDown);
        latch.await(10, TimeUnit.SECONDS);
        assertEquals(result.get().getCode(), "OK");
    }
}
