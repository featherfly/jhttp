
package cn.featherfly.jhttp.httpclient5;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Str;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.MediaType;

/**
 * Test for the rxjava client.
 *
 * @author zhongj
 */
public class HttpRxjavaClientTest {

    final String host = "http://localhost:8080/{0}";

    HttpRxjavaClientImpl http = HttpRxjavaClientImpl.builder().build();

    @Test
    public void listener() throws InterruptedException {
        AtomicLong deserializeCount = new AtomicLong();
        AtomicLong serializeCount = new AtomicLong();
        String url = Str.format(host, "user2");
        User user = new User();
        user.setName("yufei羽飞");
        user.setAge(18);
        HttpListener l1 = countingListener(deserializeCount, serializeCount);
        HttpListener l2 = countingListener(deserializeCount, serializeCount);
        HttpListener l3 = countingListener(deserializeCount, serializeCount);
        HttpRxjavaClientImpl client = HttpRxjavaClientImpl.builder().listener(l1, l2, l3).build();

        assertResult(client.post(url, user, Result.class), deserializeCount, serializeCount, 3, 3);

        deserializeCount.set(0);
        serializeCount.set(0);
        assertResult(client.post(req -> req.removeListener(l1, l2, l3).url(url).parameter(user), Result.class),
            deserializeCount, serializeCount, 0, 0);
    }

    private HttpListener countingListener(AtomicLong deserializeCount, AtomicLong serializeCount) {
        return new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                serializeCount.incrementAndGet();
            }
        };
    }

    private void assertResult(io.reactivex.rxjava3.core.Observable<Result> observable, AtomicLong deserializeCount,
        AtomicLong serializeCount, int expectedDeserialize, int expectedSerialize) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        observable.subscribe(result -> {
            try {
                assertEquals(result.getCode(), "OK");
                assertEquals(deserializeCount.get(), expectedDeserialize);
                assertEquals(serializeCount.get(), expectedSerialize);
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        }, e -> {
            error.set(e);
            latch.countDown();
        });
        latch.await(10, TimeUnit.SECONDS);
        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
    }

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
