package cn.featherfly.jhttp.jdk;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.common.serialization.Serialization;
import cn.featherfly.jhttp.HttpListener;

/**
 * Common configuration for the JDK 11 client builders.
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
    protected String contentType;

    /** The deserialize with content type. */
    protected boolean deserializeWithContentType;

    /** The code same as success. */
    protected Set<Integer> codeSameAsSuccess;

    /** The download progress chunk size, -1 means leave the default. */
    protected int downloadProgressPerSize = -1;

    /** The listeners. */
    protected final List<HttpListener> listeners = new ArrayList<>();

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
     * Set the request timeout in seconds.
     *
     * @param requestTimeout the request timeout
     * @return this builder
     */
    public B requestTimeout(long requestTimeout) {
        config.requestTimeout = requestTimeout;
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
     * Set the redirect policy.
     *
     * @param followRedirects the redirect policy
     * @return this builder
     */
    public B followRedirects(HttpClient.Redirect followRedirects) {
        config.followRedirects = followRedirects;
        return self();
    }

    /**
     * Set the HTTP version.
     *
     * @param version the HTTP version
     * @return this builder
     */
    public B version(HttpClient.Version version) {
        config.version = version;
        return self();
    }

    /**
     * Set the proxy selector.
     *
     * @param proxy the proxy selector
     * @return this builder
     */
    public B proxy(ProxySelector proxy) {
        config.proxy = proxy;
        return self();
    }

    /**
     * Set the SSL context.
     *
     * @param sslContext the SSL context
     * @return this builder
     */
    public B sslContext(SSLContext sslContext) {
        config.sslContext = sslContext;
        return self();
    }

    /**
     * Set the SSL parameters.
     *
     * @param sslParameters the SSL parameters
     * @return this builder
     */
    public B sslParameters(SSLParameters sslParameters) {
        config.sslParameters = sslParameters;
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
     * Set the request content type.
     *
     * @param contentType the content type
     * @return this builder
     */
    public B contentType(String contentType) {
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
     * Add a http listener.
     *
     * @param listener the listener
     * @return this builder
     */
    public B listener(HttpListener listener) {
        if (listener != null) {
            listeners.add(listener);
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
            contentType = JdkHttpUtils.JSON_CONTENT_TYPE;
        }
    }

    /**
     * Apply the common settings to a newly created client.
     *
     * @param client the client
     */
    protected void applyCommon(AbstractHttpClient<?> client) {
        client.setRequestTimeout(config.requestTimeout);
        client.setDeserializeWithContentType(deserializeWithContentType);
        if (codeSameAsSuccess != null) {
            client.setCodeSameAsSuccess(codeSameAsSuccess);
        }
        if (downloadProgressPerSize > 0) {
            client.setDownloadProgressPerSize(downloadProgressPerSize);
        }
        for (HttpListener listener : listeners) {
            client.on(listener);
        }
    }

    /**
     * Create the JDK provider client from this builder's config.
     *
     * @return the JDK HttpClient
     */
    protected HttpClient createClient() {
        return createClient(config);
    }

    /**
     * Create the JDK provider client from the given config.
     *
     * @param config the request config
     * @return the JDK HttpClient
     */
    public static HttpClient createClient(HttpRequestConfig config) {
        HttpRequestConfig requestConfig = config == null ? new HttpRequestConfig() : config;
        HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(
            requestConfig.connectTimeout)).followRedirects(requestConfig.followRedirects).version(
                requestConfig.version);
        if (requestConfig.executorService != null) {
            builder.executor(requestConfig.executorService);
        }
        if (requestConfig.proxy != null) {
            builder.proxy(requestConfig.proxy);
        }
        if (requestConfig.sslContext != null) {
            builder.sslContext(requestConfig.sslContext);
        }
        if (requestConfig.sslParameters != null) {
            builder.sslParameters(requestConfig.sslParameters);
        }
        return builder.build();
    }
}
