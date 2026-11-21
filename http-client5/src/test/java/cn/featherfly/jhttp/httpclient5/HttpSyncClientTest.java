
package cn.featherfly.jhttp.httpclient5;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Console;
import cn.featherfly.common.lang.Str;
import cn.featherfly.common.lang.SystemPropertyUtils;
import cn.featherfly.common.lang.WordUtils;
import cn.featherfly.common.structure.ChainMapImpl;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.Http;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.MediaType;
import cn.featherfly.jhttp.UploadFile;

/**
 * Test.
 *
 * @author zhongj
 */
public class HttpSyncClientTest {

    final String OK = "OK";

    final String host = "http://localhost:8080/{0}";

    User user;

    HttpSyncClientImpl http = HttpSyncClientImpl.builder().build();

    /**
     *
     */
    public HttpSyncClientTest() {
        user = new User();
        user.setName("yufei羽飞");
        user.setAge(18);
    }

    @Test
    public void listener() {
        AtomicLong deserializeCount = new AtomicLong(0);
        AtomicLong serializeCount = new AtomicLong(0);
        String url = Str.format(host, "user2");
        HttpListener listener = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                Console.log("onDeserialize: mediaType = {}", mediaType.toString());
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                Console.log("onSerialize: mediaType = {}", mediaType.toString());
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener2 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener3 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody, MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType) {
                serializeCount.incrementAndGet();
            }
        };

        HttpSyncClientImpl client = HttpSyncClientImpl.builder().deserializeWithContentType(true)
            .listener(listener, listener2, listener3).build();

        Result result = client.post(url, user, Result.class);
        System.out.println(result);
        assertEquals(result.getCode(), OK);
        assertEquals(deserializeCount.get(), 3);
        assertEquals(serializeCount.get(), 3);

        deserializeCount.set(0);
        serializeCount.set(0);

        // remove the listeners for this request only
        client.post(req -> req.removeListener(listener, listener2, listener3).url(url).parameter(user), Result.class);
        assertEquals(result.getCode(), OK);
        assertEquals(deserializeCount.get(), 0);
        assertEquals(serializeCount.get(), 0);

        client.shutdown();
    }

    @Test
    public void get() {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        String xml = "<User><name>yufei羽飞</name><age>18</age></User>";
        String url = Str.format(host, "user");
        assertEquals(Http.get(url), json);

        assertEquals(Http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/json")),
            json);

        assertEquals(Http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/xml")),
            xml);
    }

    @Test
    public void get1() {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        String xml = "<User><name>yufei羽飞</name><age>18</age></User>";
        String url = Str.format(host, "user");
        assertEquals(http.get(url), json);

        assertEquals(http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/json")),
            json);

        assertEquals(http.get(url, null, new ChainMapImpl<String, String>().putChain("Accept", "application/xml")),
            xml);
    }

    @Test
    public void get2() {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        String xml = "<User><name>yufei羽飞</name><age>18</age></User>";
        String url = Str.format(host, "user");

        assertEquals(http.get(rb -> rb.url(url)), json);
        assertEquals(http.get(rb -> rb.url(url).header(hb -> hb.set("Accept", "application/json"))), json);
        assertEquals(http.get(rb -> rb.url(url).header(hb -> hb.set("Accept", "application/xml"))), xml);
    }

    @Test
    public void postBody() {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        String url = Str.format(host, "user");
        assertEquals(http.post(url, json,
            new ChainMapImpl<String, String>().putChain("content-type", "application/json; charset=utf-8")), OK);
        assertEquals(http.post(url, user), OK);

        HttpSyncClientImpl xmlClient = HttpSyncClientImpl.builder().contentType(HttpClientUtils.XML_CONTENT_TYPE_OBJ)
            .build();
        assertEquals(xmlClient.post(url, user), OK);
        xmlClient.shutdown();
    }

    @Test
    public void postBody2() {
        String url = Str.format(host, "user");
        assertEquals(http.post(rb -> rb.url(url).header(hb -> hb.set("content-type", "application/json; charset=utf-8"))
            .parameter("{\"name\":\"yufei羽飞\",\"age\":18}")), OK);
        assertEquals(http.post(rb -> rb.url(url).header(hb -> hb.set("content-type", "application/json; charset=utf-8"))
            .body("{\"name\":\"yufei羽飞\",\"age\":18}")), OK);
        assertEquals(http.post(rb -> rb.url(url).parameter(user)), OK);
        assertEquals(http.post(rb -> rb.url(url).body(user)), OK);

        HttpSyncClientImpl xmlClient = HttpSyncClientImpl.builder().contentType(HttpClientUtils.XML_CONTENT_TYPE_OBJ)
            .build();
        assertEquals(xmlClient.post(rb -> rb.url(url).body(user)), OK);
        xmlClient.shutdown();
    }

    @Test
    public void cacheClient() {
        String json = "{\"name\":\"yufei羽飞\",\"age\":18}";
        HttpSyncClientImpl cached = HttpSyncClientImpl.builder().enableCache(true)
            .cacheDir(new File(SystemPropertyUtils.getJavaIoTmpdir(), "easy-httpclient5-cache")).build();
        assertEquals(cached.get(Str.format(host, "user")), json);
        cached.shutdown();
    }

    @Test
    public void upload() {
        String url = Str.format(host, "upload");
        String result = http.post(url,
            new ChainMapImpl<String, Serializable>().putChain("key", "key2")
                .putChain("file1", new UploadFile("filename1.txt", "text/plain", "abcde".getBytes()))
                .putChain("file2", new UploadFile("测试中文名.txt", "text/plain", "12345".getBytes())));
        assertEquals(result, OK);
    }

    @Test
    public void download() {
        AtomicLong count = new AtomicLong(0);
        String url = "https://mirrors.cloud.tencent.com/debian/dists/stable/main/binary-amd64/Packages.xz";
        File file = new File(SystemPropertyUtils.getJavaIoTmpdir() + "/download/" + System.currentTimeMillis());

        long size = http.download(url, file, (r, t) -> {
            count.set(r);
            System.out.print(Str.format("\r文件大小：{0}, 下载：{1}， 进度：{2}%", WordUtils.parseUnit(t), WordUtils.parseUnit(r),
                BigDecimal.valueOf(r).divide(BigDecimal.valueOf(t), 4, RoundingMode.DOWN)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN).toString()));
        });
        System.out.println();
        assertEquals(size, count.longValue());
    }
}
