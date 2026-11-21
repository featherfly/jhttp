package cn.featherfly.jhttp.jdk;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import java.io.File;

/**
 * Configuration for the JDK 11 based clients.
 *
 * @author zhongj
 */
public class HttpRequestConfig {

    /** The connect timeout in seconds. */
    public long connectTimeout = 60;

    /** The request timeout in seconds. */
    public long requestTimeout = 60;

    /** The executor used by the JDK HttpClient. */
    public ExecutorService executorService;

    /** The redirect policy. */
    public HttpClient.Redirect followRedirects = HttpClient.Redirect.NEVER;

    /** The HTTP protocol version. */
    public HttpClient.Version version = HttpClient.Version.HTTP_1_1;

    /** The optional proxy selector. */
    public ProxySelector proxy;

    /** The optional SSL context. */
    public SSLContext sslContext;

    /** The optional SSL parameters. */
    public SSLParameters sslParameters;

    /**
     * Instantiates a new http request config.
     */
    public HttpRequestConfig() {
    }

    /**
     * Instantiates a new http request config.
     *
     * @param cacheDir kept for signature parity with the other modules; JDK 11
     *        HttpClient has no built-in cache
     */
    public HttpRequestConfig(File cacheDir) {
    }
}
