package cn.featherfly.jhttp.jdk;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import cn.featherfly.common.lang.Str;
import cn.featherfly.http.server.Result;
import cn.featherfly.http.server.User;

/**
 * Test for the JDK 11 RxJava client.
 *
 * @author zhongj
 */
public class HttpRxjavaClientTest {

    final String host = "http://localhost:8080/{0}";

    HttpRxjavaClientImpl http = HttpRxjavaClientImpl.builder().build();

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
        });
        latch.await(10, TimeUnit.SECONDS);
        assertEquals(result.get(), json);
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
        });
        latch.await(10, TimeUnit.SECONDS);
        assertEquals(result.get().getCode(), "OK");
    }

    @Test
    public void downloadToFile() throws InterruptedException {
        String url = "https://mirrors.cloud.tencent.com/debian/dists/stable/main/binary-amd64/Packages.xz";
        File file = new File(System.getProperty("java.io.tmpdir"), "easy-jdk11-rx-" + System.currentTimeMillis());
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
        assertEquals(file.delete(), true, "download output stream was not closed");
    }
}
