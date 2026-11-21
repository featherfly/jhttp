
package cn.featherfly.jhttp.httpclient5;

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
import cn.featherfly.common.lang.WordUtils;
import cn.featherfly.common.structure.ChainMapImpl;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.UploadFile;

/**
 * Test.
 *
 * @author zhongj
 */
public class HttpAsyncTest {

    final String OK = "OK";

    final String host = "http://localhost:8080/{0}";

    User user;

    HttpAsyncClientImpl http = HttpAsyncClientImpl.builder().build();

    /**
     *
     */
    public HttpAsyncTest() {
        user = new User();
        user.setName("yufei羽飞");
        user.setAge(18);
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

        HttpAsyncClientImpl xmlClient = HttpAsyncClientImpl.builder().contentType(HttpClientUtils.XML_CONTENT_TYPE_OBJ)
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
        http.download(url, file, (r, t) -> {
            count.set(r);
            System.out.print(Str.format("\r文件大小：{0}, 下载：{1}", WordUtils.parseUnit(t), WordUtils.parseUnit(r)));
        }).completion(result -> {
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

    private String awaitString(cn.featherfly.jhttp.HttpRequestCompletion<String> completion)
        throws InterruptedException {
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
