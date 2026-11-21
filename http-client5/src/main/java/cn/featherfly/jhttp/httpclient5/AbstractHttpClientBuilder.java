package cn.featherfly.jhttp.httpclient5;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.cache.CachingHttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.util.Timeout;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.common.serialization.Serialization;
import cn.featherfly.jhttp.HttpListener;

/**
 * Common configuration for the httpclient5 based client builders.
 *
 * @author zhongj
 * @param <B> the concrete builder type
 */
public abstract class AbstractHttpClientBuilder<B extends AbstractHttpClientBuilder<B>> {

    /** The request config. */
    protected HttpRequestConfig config = new HttpRequestConfig();

    /** The default headers. */
    protected Map<String, String> defaultHeaders = new HashMap<>();

    /** The serialization strategy. */
    protected SerializableStrategy serialization;

    /** The request content type. */
    protected ContentType contentType;

    /** The deserialize with content type. */
    protected boolean deserializeWithContentType;

    /** The code same as success. */
    protected Set<Integer> codeSameAsSuccess;

    /** The download progress chunk size, -1 means leave the default. */
    protected int downloadProgressPerSize = -1;

    /** The listeners. */
    protected final Set<HttpListener> listeners = new LinkedHashSet<>();

    /**
     * Gets the concrete builder.
     *
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    /**
     * Set the whole request config.
     *
     * @param config the config
     * @return this builder
     */
    public B config(HttpRequestConfig config) {
        this.config = config == null ? new HttpRequestConfig() : config;
        return self();
    }

    /**
     * Set the connect timeout in seconds.
     *
     * @param connectTimeout the connect timeout
     * @return this builder
     */
    public B connectTimeout(long connectTimeout) {
        config.connectTimeout = connectTimeout;
        return self();
    }

    /**
     * Set the socket (response) timeout in seconds.
     *
     * @param socketTimeout the socket timeout
     * @return this builder
     */
    public B socketTimeout(long socketTimeout) {
        config.socketTimeout = socketTimeout;
        return self();
    }

    /**
     * Set the connection request timeout in seconds.
     *
     * @param connectionRequestTimeout the connection request timeout
     * @return this builder
     */
    public B connectionRequestTimeout(long connectionRequestTimeout) {
        config.connectionRequestTimeout = connectionRequestTimeout;
        return self();
    }

    /**
     * Set the max total connections.
     *
     * @param maxConnTotal the max total connections
     * @return this builder
     */
    public B maxConnTotal(int maxConnTotal) {
        config.maxConnTotal = maxConnTotal;
        return self();
    }

    /**
     * Set the max connections per route.
     *
     * @param maxConnPerRoute the max connections per route
     * @return this builder
     */
    public B maxConnPerRoute(int maxConnPerRoute) {
        config.maxConnPerRoute = maxConnPerRoute;
        return self();
    }

    /**
     * Enable or disable the response cache.
     *
     * @param enableCache true to enable
     * @return this builder
     */
    public B enableCache(boolean enableCache) {
        config.enableCache = enableCache;
        return self();
    }

    /**
     * Set the cache dir.
     *
     * @param cacheDir the cache dir
     * @return this builder
     */
    public B cacheDir(File cacheDir) {
        config.cacheDir = cacheDir;
        return self();
    }

    /**
     * Set the cache max size.
     *
     * @param cacheMaxSize the cache max size
     * @return this builder
     */
    public B cacheMaxSize(long cacheMaxSize) {
        config.cacheMaxSize = cacheMaxSize;
        return self();
    }

    /**
     * Set the executor service.
     *
     * @param executorService the executor service
     * @return this builder
     */
    public B executorService(ExecutorService executorService) {
        config.executorService = executorService;
        return self();
    }

    /**
     * Set the default headers.
     *
     * @param defaultHeaders the default headers
     * @return this builder
     */
    public B defaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders == null ? new HashMap<>() : defaultHeaders;
        return self();
    }

    /**
     * Set the serialization strategy.
     *
     * @param serialization the serialization
     * @return this builder
     */
    public B serialization(SerializableStrategy serialization) {
        this.serialization = serialization;
        return self();
    }

    /**
     * Set the default request content type.
     *
     * @param contentType the content type
     * @return this builder
     */
    public B contentType(ContentType contentType) {
        this.contentType = contentType;
        return self();
    }

    /**
     * Set whether to choose the deserializer by the response content-type.
     *
     * @param deserializeWithContentType true to enable
     * @return this builder
     */
    public B deserializeWithContentType(boolean deserializeWithContentType) {
        this.deserializeWithContentType = deserializeWithContentType;
        return self();
    }

    /**
     * Set the status codes which are treated as success in addition to 200.
     *
     * @param codeSameAsSuccess the status codes
     * @return this builder
     */
    public B codeSameAsSuccess(Set<Integer> codeSameAsSuccess) {
        this.codeSameAsSuccess = codeSameAsSuccess;
        return self();
    }

    /**
     * Add a success status code.
     *
     * @param code the status code
     * @return this builder
     */
    public B codeAsSuccess(int code) {
        if (codeSameAsSuccess == null) {
            codeSameAsSuccess = new HashSet<>();
        }
        codeSameAsSuccess.add(code);
        return self();
    }

    /**
     * Set the download progress chunk size.
     *
     * @param downloadProgressPerSize the chunk size in bytes
     * @return this builder
     */
    public B downloadProgressPerSize(int downloadProgressPerSize) {
        this.downloadProgressPerSize = downloadProgressPerSize;
        return self();
    }

    /**
     * Add http listeners.
     *
     * @param listeners the listeners
     * @return this builder
     */
    public B listener(HttpListener... listeners) {
        if (listeners != null) {
            for (HttpListener listener : listeners) {
                this.listeners.add(listener);
            }
        }
        return self();
    }

    /**
     * Fill in the default configuration before building a client.
     */
    protected void prepareToBuild() {
        if (serialization == null) {
            serialization = Serialization.getDefault();
        }
        if (contentType == null) {
            contentType = HttpClientUtils.JSON_CONTENT_TYPE_OBJ;
        }
    }

    /**
     * Apply the common settings to a newly created client.
     *
     * @param client the client
     */
    protected void applyCommon(AbstractHttpClient<?> client) {
        client.setDeserializeWithContentType(deserializeWithContentType);
        if (codeSameAsSuccess != null) {
            client.setCodeSameAsSuccess(codeSameAsSuccess);
        }
        if (downloadProgressPerSize > 0) {
            client.setDownloadProgressPerSize(downloadProgressPerSize);
        }
    }

    /**
     * Create the classic HttpClient5 provider client from the given config.
     *
     * @param config the request config
     * @return the classic client
     */
    public static CloseableHttpClient createClassicClient(HttpRequestConfig config) {
        HttpRequestConfig requestConfig = config == null ? new HttpRequestConfig() : config;
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(requestConfig.connectionRequestTimeout))
            .setResponseTimeout(Timeout.ofSeconds(requestConfig.socketTimeout)).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(requestConfig.connectTimeout)).build();
        org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder
            .create().setMaxConnTotal(requestConfig.maxConnTotal).setMaxConnPerRoute(requestConfig.maxConnPerRoute)
            .setDefaultConnectionConfig(connectionConfig).build();
        HttpClientBuilder builder;
        if (requestConfig.enableCache && requestConfig.cacheDir != null) {
            builder = CachingHttpClientBuilder.create().setCacheDir(requestConfig.cacheDir);
        } else {
            builder = HttpClients.custom();
        }
        return builder.setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
    }

    /**
     * Create the async HttpClient5 provider client from the given config (not started yet).
     *
     * @param config the request config
     * @return the async client
     */
    public static CloseableHttpAsyncClient createAsyncClient(HttpRequestConfig config) {
        HttpRequestConfig requestConfig = config == null ? new HttpRequestConfig() : config;
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(requestConfig.connectionRequestTimeout))
            .setResponseTimeout(Timeout.ofSeconds(requestConfig.socketTimeout)).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(requestConfig.connectTimeout)).build();
        org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager connectionManager = org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
            .create().setMaxConnTotal(requestConfig.maxConnTotal).setMaxConnPerRoute(requestConfig.maxConnPerRoute)
            .setDefaultConnectionConfig(connectionConfig).build();
        return HttpAsyncClients.custom().setDefaultRequestConfig(defaultRequestConfig)
            .setConnectionManager(connectionManager).build();
    }

    /**
     * Build the classic client from this builder's config.
     *
     * @return the classic client
     */
    protected CloseableHttpClient buildClassicClient() {
        return createClassicClient(config);
    }

    /**
     * Build the async client from this builder's config (not started yet).
     *
     * @return the async client
     */
    protected CloseableHttpAsyncClient buildAsyncClient() {
        return createAsyncClient(config);
    }
}
