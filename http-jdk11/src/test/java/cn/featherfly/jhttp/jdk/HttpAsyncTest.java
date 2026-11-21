package cn.featherfly.jhttp.jdk;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.lang.SystemPropertyUtils;
import cn.featherfly.common.structure.ChainMapImpl;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpRequestCompletion;
import cn.featherfly.jhttp.MediaType;
import cn.featherfly.jhttp.UploadFile;

/**
 * Test for the JDK 11 async client.
 *
 * @author zhongj
 */
public class HttpAsyncTest {

    final String OK = "OK";

    final String host = "http://localhost:8080/{0}";

    User user;

    HttpAsyncClientImpl http = HttpAsyncClientImpl.builder().build();

    /**
     * Instantiates the test.
     */
    public HttpAsyncTest() {
        user = new User();
        user.setName("yufei羽飞");
        user.setAge(18);
    }

    @Test
    public void listener() throws InterruptedException {
        AtomicLong deserializeCount = new AtomicLong();
        AtomicLong serializeCount = new AtomicLong();
        String url = Str.format(host, "user2");
        HttpListener l1 = countingListener(deserializeCount, serializeCount);
        HttpListener l2 = countingListener(deserializeCount, serializeCount);
        HttpListener l3 = countingListener(deserializeCount, serializeCount);
        HttpAsyncClientImpl client = HttpAsyncClientImpl.builder().listener(l1, l2, l3).build();

        assertResult(client.post(url, user, Result.class), OK, deserializeCount, serializeCount, 3, 3);

        deserializeCount.set(0);
        serializeCount.set(0);
        assertResult(client.post(req -> req.removeListener(l1, l2, l3).url(url).parameter(user), Result.class), OK,
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

    private void assertResult(HttpRequestCompletion<Result> completion, String expectedCode,
        AtomicLong deserializeCount, AtomicLong serializeCount, int expectedDeserialize, int expectedSerialize)
        throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        completion.completion(result -> {
            try {
                assertEquals(result.getCode(), expectedCode);
                assertEquals(deserializeCount.get(), expectedDeserialize);
                assertEquals(serializeCount.get(), expectedSerialize);
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        }, er -> {
            error.set(new RuntimeException(er.getMessage()));
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
        String xml = "<User><name>yufei羽飞</name><age>18</age></User>";
        String url = Str.format(host, "user");
        assertEquals(awaitString(http.get(url)), json);
        assertEquals(
            awaitString(http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/json"))),
            json);
        assertEquals(
            awaitString(http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/xml"))),
            xml);
    }

    @Test
    public void postBody() throws InterruptedException {
        String url = Str.format(host, "user");
        assertEquals(awaitString(http.post(url, "{\"name\":\"yufei羽飞\",\"age\":18}",
            new ChainMapImpl<String, String>().putChain("content-type", "application/json; charset=utf-8"))), OK);
        assertEquals(awaitString(http.post(url, user)), OK);

        HttpAsyncClientImpl xmlClient = HttpAsyncClientImpl.builder().contentType(JdkHttpUtils.XML_CONTENT_TYPE)
            .build();
        assertEquals(awaitString(xmlClient.post(url, user)), OK);
    }

    @Test
    public void upload() throws InterruptedException {
        String url = Str.format(host, "upload");
        assertEquals(awaitString(http.post(url,
            new ChainMapImpl<String, Serializable>().putChain("key", "key2")
                .putChain("file1", new UploadFile("filename1.txt", "text/plain", "abcde".getBytes()))
                .putChain("file2", new UploadFile("测试中文名.txt", "text/plain", "12345".getBytes())))),
            OK);
    }

    @Test
    public void download() throws InterruptedException {
        AtomicLong count = new AtomicLong(0);
        String url = "https://mirrors.cloud.tencent.com/debian/dists/stable/main/binary-amd64/Packages.xz";
        File file = new File(SystemPropertyUtils.getJavaIoTmpdir() + "/download/" + System.currentTimeMillis());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Long> size = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        http.download(url, file, (r, t) -> count.set(r)).completion(result -> {
            size.set(result);
            latch.countDown();
        }, er -> {
            error.set(new RuntimeException(er.getMessage()));
            latch.countDown();
        });
        latch.await(60, TimeUnit.SECONDS);
        if (error.get() != null) {
            throw new AssertionError("download failed: " + error.get().getMessage());
        }
        assertEquals(size.get(), count.longValue());
    }

    private String awaitString(HttpRequestCompletion<String> completion) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        completion.completion(r -> {
            result.set(r);
            latch.countDown();
        }, er -> {
            error.set(new RuntimeException(er.getMessage()));
            latch.countDown();
        }).exceptionally(t -> {
            error.set(t);
            latch.countDown();
            return t.getMessage();
        });
        latch.await(10, TimeUnit.SECONDS);
        if (error.get() != null) {
            throw new AssertionError("request failed: " + error.get().getMessage(), error.get());
        }
        return result.get();
    }
}
