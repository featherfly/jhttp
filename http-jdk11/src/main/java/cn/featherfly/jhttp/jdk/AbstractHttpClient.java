package cn.featherfly.jhttp.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.featherfly.common.lang.AssertIllegalArgument;
import cn.featherfly.common.lang.Lang;
import cn.featherfly.common.lang.Str;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.common.serialization.SerializationException;
import cn.featherfly.common.serialization.SerializationExceptionCode;
import cn.featherfly.common.serialization.Serializer;
import cn.featherfly.jhttp.HttpCode;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpDownloadClient.ProgressConsumer;

/**
 * Shared engine for the JDK 11 HTTP client implementations.
 *
 * @author zhongj
 * @param <C> the concrete client type
 */
public abstract class AbstractHttpClient<C extends AbstractHttpClient<C>> {

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The JDK provider client. */
    protected java.net.http.HttpClient client;

    /** The request timeout in seconds. */
    protected long requestTimeout = 60;

    /** The deserialize with content type. */
    protected boolean deserializeWithContentType;

    /** The serialization. */
    protected SerializableStrategy serialization;

    /** The serializer. */
    private Serializer serializer;

    /** The request content type. */
    private String mediaType;

    /** The text plain content type. */
    protected String textPlain;

    /** The default headers. */
    protected Map<String, String> headersMap = new HashMap<>();

    /** The code same as success. */
    private Set<Integer> codeSameAsSuccess = new HashSet<>();

    /** The download progress per size. */
    protected int downloadProgressPerSize = 1024 * 256;

    /** The listeners. */
    protected final Set<HttpListener> listeners = new LinkedHashSet<>(0);

    /**
     * Instantiates a new http client.
     *
     * @param client the JDK provider client
     * @param headers the default headers
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected AbstractHttpClient(java.net.http.HttpClient client, Map<String, String> headers,
        SerializableStrategy serialization, String contentType) {
        init(client, headers, serialization, contentType);
    }

    /**
     * Inits the client.
     *
     * @param client the JDK provider client
     * @param headers the default headers
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected void init(java.net.http.HttpClient client, Map<String, String> headers,
        SerializableStrategy serialization, String contentType) {
        AssertIllegalArgument.isNotNull(client, "client");
        AssertIllegalArgument.isNotNull(serialization, "serialization");
        AssertIllegalArgument.isNotNull(contentType, "contentType");
        this.client = client;
        this.serialization = serialization;
        mediaType = contentType;
        Charset charset = JdkHttpUtils.charsetOf(contentType);
        textPlain = "text/plain; charset=" + charset.name();
        this.serialization.setCharset(charset);
        serializer = getSerializer(JdkHttpUtils.baseType(contentType), true);
        if (headers != null) {
            headersMap.putAll(headers);
        }
    }

    /**
     * Set the request timeout in seconds.
     *
     * @param requestTimeout the request timeout
     */
    public void setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    /**
     * Shutdown. The JDK 11 HttpClient has no close method; its selector threads are
     * daemon threads and are reclaimed when the client becomes unreachable.
     */
    public void shutdown() {
        // nothing to close
    }

    /**
     * get deserializeWithContentType value.
     *
     * @return deserializeWithContentType
     */
    public boolean isDeserializeWithContentType() {
        return deserializeWithContentType;
    }

    /**
     * set deserializeWithContentType value.
     *
     * @param deserializeWithContentType deserializeWithContentType
     */
    public void setDeserializeWithContentType(boolean deserializeWithContentType) {
        this.deserializeWithContentType = deserializeWithContentType;
    }

    /**
     * get codeSameAsSuccess value.
     *
     * @return codeSameAsSuccess
     */
    public Set<Integer> getCodeSameAsSuccess() {
        return codeSameAsSuccess;
    }

    /**
     * set codeSameAsSuccess value.
     *
     * @param codeSameAsSuccess codeSameAsSuccess
     */
    public void setCodeSameAsSuccess(Set<Integer> codeSameAsSuccess) {
        this.codeSameAsSuccess = codeSameAsSuccess;
    }

    /**
     * set downloadProgressPerSize value.
     *
     * @param downloadProgressPerSize the chunk size
     */
    public void setDownloadProgressPerSize(int downloadProgressPerSize) {
        this.downloadProgressPerSize = downloadProgressPerSize;
    }

    /**
     * Serialize a request body.
     *
     * @param requestBody the request body
     * @return the bytes
     */
    protected byte[] serialize(Object requestBody) {
        Charset charset = JdkHttpUtils.charsetOf(mediaType);
        if (requestBody instanceof String) {
            return ((String) requestBody).getBytes(charset);
        } else if (requestBody instanceof byte[]) {
            return (byte[]) requestBody;
        }
        byte[] body = serializer.serialize(requestBody);
        if (!listeners.isEmpty()) {
            for (HttpListener listener : listeners) {
                listener.onSerialize(requestBody, body, JdkHttpUtils.create(mediaType));
            }
        }
        return body;
    }

    /**
     * Deserialize a response body.
     *
     * @param <T> the response type
     * @param responseBody the response body
     * @param contentType the response content type
     * @param responseType the response type
     * @return the deserialized object
     */
    protected <T> T deserialize(byte[] responseBody, String contentType, Class<T> responseType) {
        Serializer resSerializer = serializer;
        if (deserializeWithContentType && Lang.isNotEmpty(contentType)) {
            Serializer chosen = getSerializer(JdkHttpUtils.baseType(contentType), false);
            if (chosen != null) {
                resSerializer = chosen;
            }
        }
        T result = resSerializer.deserialize(responseBody, responseType);
        if (!listeners.isEmpty()) {
            for (HttpListener listener : listeners) {
                listener.onDeserialize(responseBody, result,
                    JdkHttpUtils.create(contentType == null ? mediaType : contentType));
            }
        }
        return result;
    }

    /**
     * Gets the serializer for a base media type.
     *
     * @param baseType the base media type
     * @param throwExceptionNoSerializer whether to throw when missing
     * @return the serializer, possibly null
     */
    protected Serializer getSerializer(String baseType, boolean throwExceptionNoSerializer) {
        Serializer mimeTypeSerializer = serialization.getSerializer(baseType);
        if (mimeTypeSerializer != null) {
            return mimeTypeSerializer;
        }
        if (throwExceptionNoSerializer) {
            throw new SerializationException(SerializationExceptionCode.createNoSerializerForMimeTypeCode(baseType));
        }
        logger.warn("no serializer found for content-type {}", baseType);
        return null;
    }

    /**
     * Creates the request headers.
     *
     * @param headers the per-request headers
     * @return the merged headers
     */
    protected Map<String, String> createHeaders(Map<String, String> headers) {
        Map<String, String> result = new HashMap<>(headersMap);
        if (Lang.isNotEmpty(headers)) {
            result.putAll(headers);
        }
        return result;
    }

    /**
     * Creates the request headers, deriving content-type from the body.
     *
     * @param headers the per-request headers
     * @param requestObject the request body
     * @return the merged headers
     */
    protected Map<String, String> createHeaders(Map<String, String> headers, Object requestObject) {
        Map<String, String> result = createHeaders(headers);
        if (requestObject instanceof String) {
            if (!result.containsKey("content-type")) {
                result.put("content-type", textPlain);
            }
        } else if (requestObject instanceof byte[]) {
            result.put("content-type", JdkHttpUtils.STREAM_CONTENT_TYPE);
        } else {
            result.put("content-type", mediaType);
        }
        return result;
    }

    /**
     * Gets the content type of the request body.
     *
     * @param requestObject the request body
     * @param headers the headers
     * @return the content type
     */
    protected String getContentType(Object requestObject, Map<String, String> headers) {
        if (requestObject instanceof String) {
            return headers.containsKey("content-type") ? mediaType : textPlain;
        } else if (requestObject instanceof byte[]) {
            return JdkHttpUtils.STREAM_CONTENT_TYPE;
        }
        return mediaType;
    }

    /**
     * Checks if is success.
     *
     * @param code the status code
     * @return true if success
     */
    protected boolean isSuccess(int code) {
        return code == HttpCode.OK || codeSameAsSuccess.contains(code);
    }

    /**
     * Checks if is success (2xx), used by downloads.
     *
     * @param code the status code
     * @return true if success
     */
    protected boolean is2xxSuccess(int code) {
        return code >= 200 && code < 300;
    }

    /**
     * Creates a response adapter.
     *
     * @param response the JDK response
     * @param networkTime the network time
     * @return the response adapter
     */
    protected cn.featherfly.jhttp.HttpResponse response(HttpResponse<byte[]> response, long networkTime) {
        return response(response.statusCode(), response.body(), JdkHttpUtils.headersToMap(response.headers().map()),
            networkTime);
    }

    /**
     * Creates a response adapter from its parts.
     *
     * @param statusCode the status code
     * @param data the response body
     * @param headers the response headers
     * @param networkTime the network time
     * @return the response adapter
     */
    protected cn.featherfly.jhttp.HttpResponse response(int statusCode, byte[] data, Map<String, String> headers,
        long networkTime) {
        return new ResponseAdapter(statusCode, data, headers, networkTime);
    }

    /**
     * Executes a request and returns the response on success.
     *
     * @param request the request
     * @return the response
     */
    protected HttpResponse<byte[]> getSuccessResponse(HttpRequest request) {
        long start = System.currentTimeMillis();
        try {
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            long networkTime = System.currentTimeMillis() - start;
            if (isSuccess(response.statusCode())) {
                return response;
            }
            cn.featherfly.jhttp.HttpResponse errorResponse = response(response, networkTime);
            throw new HttpErrorResponseException(errorMessage(request, response.statusCode()), errorResponse);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new HttpException(e);
        }
    }

    /**
     * The error message for a failed request.
     *
     * @param request the request
     * @param code the status code
     * @return the message
     */
    protected String errorMessage(HttpRequest request, int code) {
        return Str.format("{0} error, code {1}, message {2}", request.uri(), code, "");
    }

    /**
     * Creates a request builder with the given url and headers.
     *
     * @param url the url
     * @param headers the headers
     * @return the builder
     */
    protected HttpRequest.Builder requestBuilder(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(JdkHttpUtils.uri(url))
            .timeout(Duration.ofSeconds(requestTimeout));
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        return builder;
    }

    /**
     * Creates a download request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the request
     */
    protected HttpRequest downloadRequest(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).GET().build();
            case HEAD:
                return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody()).build();
            case DELETE:
                return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE().build();
            case POST:
            case PUT:
            case PATCH: {
                JdkHttpUtils.Body body = JdkHttpUtils.createRequestBody(params);
                Map<String, String> allHeaders = createHeaders(headers);
                allHeaders.put("content-type", body.contentType);
                HttpRequest.Builder builder = requestBuilder(url, allHeaders);
                HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofByteArray(body.content);
                switch (httpMethod) {
                    case POST:
                        return builder.POST(publisher).build();
                    case PUT:
                        return builder.PUT(publisher).build();
                    default:
                        return builder.method("PATCH", publisher).build();
                }
            }
            default:
                throw new HttpException("download unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * Executes a download with the JDK client.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @param progress the progress consumer
     * @return the number of bytes written
     */
    protected long doDownload(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        HttpRequest request = downloadRequest(httpMethod, url, params, headers);
        long start = System.currentTimeMillis();
        try {
            HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
            if (is2xxSuccess(response.statusCode())) {
                try (InputStream is = response.body()) {
                    byte[] bs = new byte[downloadProgressPerSize];
                    long total = response.headers().firstValue("content-length").map(Long::parseLong).orElse(-1L);
                    int len;
                    long readed = 0;
                    while ((len = is.read(bs)) != -1) {
                        readed += len;
                        progress.accept(readed, total);
                        output.write(bs, 0, len);
                    }
                    return readed;
                }
            }
            byte[] errorBody;
            try (InputStream is = response.body()) {
                errorBody = is.readAllBytes();
            }
            throw new HttpErrorResponseException(errorMessage(request, response.statusCode()),
                response(response.statusCode(), errorBody, JdkHttpUtils.headersToMap(response.headers().map()),
                    System.currentTimeMillis() - start));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new HttpException(e);
        }
    }

    /**
     * On.
     *
     * @param listener the listener
     * @return the client
     */
    @SuppressWarnings("unchecked")
    public C on(HttpListener listener) {
        listeners.add(listener);
        return (C) this;
    }

    /**
     * Removes the.
     *
     * @param listener the listener
     * @return the client
     */
    @SuppressWarnings("unchecked")
    public C un(HttpListener listener) {
        listeners.remove(listener);
        return (C) this;
    }

    /**
     * The response adapter.
     */
    private static class ResponseAdapter implements cn.featherfly.jhttp.HttpResponse {

        private final int statusCode;

        private final byte[] data;

        private final Map<String, String> headers;

        private final long networkTime;

        ResponseAdapter(int statusCode, byte[] data, Map<String, String> headers, long networkTime) {
            this.statusCode = statusCode;
            this.data = data;
            this.headers = headers;
            this.networkTime = networkTime;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public byte[] getData() {
            return data;
        }

        @Override
        public Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        public long getNetworkTime() {
            return networkTime;
        }
    }
}
