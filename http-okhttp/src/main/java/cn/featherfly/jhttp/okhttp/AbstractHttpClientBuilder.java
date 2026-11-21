package cn.featherfly.jhttp.okhttp;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.common.serialization.Serialization;
import cn.featherfly.jhttp.HttpListener;
import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Common configuration for the okhttp3 based client builders.
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

    /** The request media type. */
    protected MediaType mediaType;

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
     * Set the max requests.
     *
     * @param maxRequests the max requests
     * @return this builder
     */
    public B maxRequests(int maxRequests) {
        config.maxRequests = maxRequests;
        return self();
    }

    /**
     * Set the max requests per host.
     *
     * @param maxRequestsPerHost the max requests per host
     * @return this builder
     */
    public B maxRequestsPerHost(int maxRequestsPerHost) {
        config.maxRequestsPerHost = maxRequestsPerHost;
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
     * Set the request media type.
     *
     * @param mediaType the media type
     * @return this builder
     */
    public B mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
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
     * Add a http listener.
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
     * Create the okhttp3 provider client from the given config.
     *
     * @param config the request config
     * @return the okhttp client
     */
    public static OkHttpClient createClient(HttpRequestConfig config) {
        HttpRequestConfig requestConfig = config == null ? new HttpRequestConfig() : config;
        Dispatcher dispatcher = new Dispatcher(requestConfig.executorService);
        dispatcher.setMaxRequests(requestConfig.maxRequests);
        dispatcher.setMaxRequestsPerHost(requestConfig.maxRequestsPerHost);
        return new OkHttpClient.Builder().cache(new Cache(requestConfig.cacheDir, requestConfig.cacheMaxSize))
            .dispatcher(dispatcher).connectTimeout(requestConfig.connectTimeout, TimeUnit.SECONDS).build();
    }

    /**
     * Create the okhttp3 provider client from this builder's config.
     *
     * @return the okhttp client
     */
    protected OkHttpClient createClient() {
        return createClient(config);
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
     * Prepare to build.
     */
    protected void prepareToBuild() {
        if (serialization == null) {
            serialization = Serialization.getDefault();
        }
        if (mediaType == null) {
            mediaType = OkHttpUtils.JSON_MEDIA_TYPE;
        }
    }
}
