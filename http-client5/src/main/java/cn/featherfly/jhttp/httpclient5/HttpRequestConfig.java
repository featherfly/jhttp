package cn.featherfly.jhttp.httpclient5;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * The Class HttpRequestConfig.
 *
 * @author zhongj
 */
public class HttpRequestConfig {

    /** The connect timeout in seconds. */
    public long connectTimeout = 60;

    /** The socket (response) timeout in seconds. */
    public long socketTimeout = 60;

    /** The connection request timeout in seconds. */
    public long connectionRequestTimeout = 60;

    /** The max total connections. */
    public int maxConnTotal = 64;

    /** The max connections per route. */
    public int maxConnPerRoute = 5;

    /** Whether to enable the response cache. */
    public boolean enableCache = false;

    /** The cache dir. */
    public File cacheDir;

    /** The cache max size (only used by the builder when a cache dir is set). */
    public long cacheMaxSize = 5 * 1024 * 1024;

    /** The executor service (kept for signature parity with the okhttp3 config). */
    public ExecutorService executorService;

    /**
     * Instantiates a new http request config.
     */
    public HttpRequestConfig() {
        this(new File(System.getProperty("java.io.tmpdir")));
    }

    /**
     * Instantiates a new http request config.
     *
     * @param cacheDir the cache dir
     */
    public HttpRequestConfig(File cacheDir) {
        this.cacheDir = cacheDir;
    }
}
