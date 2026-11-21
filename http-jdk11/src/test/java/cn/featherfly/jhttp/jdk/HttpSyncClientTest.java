package cn.featherfly.jhttp.jdk;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.lang.SystemPropertyUtils;
import cn.featherfly.common.lang.WordUtils;
import cn.featherfly.common.structure.ChainMapImpl;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;
import cn.featherfly.jhttp.Http;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.UploadFile;

/**
 * Test for the JDK 11 sync client.
 *
 * @author zhongj
 */
public class HttpSyncClientTest {

    final String OK = "OK";

    final String host = "http://localhost:8080/{0}";

    User user;

    HttpSyncClientImpl http = HttpSyncClientImpl.builder().build();

    /**
     * Instantiates the test.
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
            public void onDeserialize(byte[] responseBody, Object deserializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener2 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
                serializeCount.incrementAndGet();
            }
        };
        HttpListener listener3 = new HttpListener() {
            @Override
            public void onDeserialize(byte[] responseBody, Object deserializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
                deserializeCount.incrementAndGet();
            }

            @Override
            public void onSerialize(Object requestBody, byte[] serializeBody,
                cn.featherfly.jhttp.MediaType mediaType) {
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
    public void postBody() {
        String url = Str.format(host, "user");
        assertEquals(http.post(url, "{\"name\":\"yufei羽飞\",\"age\":18}",
            new ChainMapImpl<String, String>().putChain("content-type", "application/json; charset=utf-8")), OK);
        assertEquals(http.post(url, user), OK);

        HttpSyncClientImpl xmlClient = HttpSyncClientImpl.builder().contentType(JdkHttpUtils.XML_CONTENT_TYPE).build();
        assertEquals(xmlClient.post(url, user), OK);
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
        assertEquals(size, count.longValue());
    }
}
