package cn.featherfly.jhttp.httpclient5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.featherfly.common.lang.Lang;
import cn.featherfly.common.lang.Str;
import cn.featherfly.common.structure.ChainSetImpl;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.common.serialization.SerializationException;
import cn.featherfly.common.serialization.SerializationExceptionCode;
import cn.featherfly.common.serialization.Serializer;
import cn.featherfly.jhttp.HttpCode;
import cn.featherfly.jhttp.HttpDownloadClient.ProgressConsumer;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpResponse;

/**
 * abstract http client.
 *
 * @author zhongj
 * @param <C> the generic type
 */
public abstract class AbstractHttpClient<C extends AbstractHttpClient<C>> {

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The classic client. */
    protected CloseableHttpClient client;

    /** The async client (created lazily). */
    private volatile CloseableHttpAsyncClient asyncClient;

    /** The deserialize with content type. */
    protected boolean deserializeWithContentType;

    /** The serialization. */
    protected SerializableStrategy serialization;

    /** The serializer. */
    private Serializer serializer;

    /** The default request media type. */
    private ContentType mediaType;

    /** The text plain media type. */
    protected ContentType textPlain;

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
     * @param client the classic provider client
     * @param asyncClient the async provider client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected AbstractHttpClient(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, Set<HttpListener> listeners, SerializableStrategy serialization,
        ContentType contentType) {
        init(client, asyncClient, headers, listeners, serialization, contentType);
    }

    /**
     * Inits the client from the provider clients created by the builder.
     *
     * @param client the classic client
     * @param asyncClient the async client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected void init(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient, Map<String, String> headers,
        Set<HttpListener> listeners, SerializableStrategy serialization, ContentType contentType) {
        cn.featherfly.common.lang.AssertIllegalArgument.isNotNull(client, "client");
        cn.featherfly.common.lang.AssertIllegalArgument.isNotNull(asyncClient, "asyncClient");
        cn.featherfly.common.lang.AssertIllegalArgument.isNotNull(serialization, "serialization");
        cn.featherfly.common.lang.AssertIllegalArgument.isNotNull(contentType, "contentType");
        this.client = client;
        this.asyncClient = asyncClient;
        this.serialization = serialization;
        mediaType = contentType;
        if (Lang.isNotEmpty(listeners)) {
            this.listeners.addAll(listeners);
        }
        if (mediaType.getCharset() != null) {
            textPlain = ContentType.create("text/plain", mediaType.getCharset());
            this.serialization.setCharset(mediaType.getCharset());
        } else {
            textPlain = ContentType.TEXT_PLAIN;
        }
        serializer = getSerializer(mediaType, true);
        if (headers != null) {
            headersMap.putAll(headers);
        }
    }

    /**
     * Gets the async client, starting it on first use.
     *
     * @return the async client
     */
    protected synchronized CloseableHttpAsyncClient asyncClient() {
        if (asyncClient.getStatus() == org.apache.hc.core5.reactor.IOReactorStatus.INACTIVE) {
            asyncClient.start();
        }
        return asyncClient;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        try {
            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        if (asyncClient != null) {
            try {
                asyncClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
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
     * Set the chunk size used by download progress reporting.
     *
     * @param downloadProgressPerSize the chunk size in bytes
     */
    public void setDownloadProgressPerSize(int downloadProgressPerSize) {
        this.downloadProgressPerSize = downloadProgressPerSize;
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
     * serialize.
     *
     * @param requestBody the request body
     * @param listeners the listeners for this request
     * @return the serialized byte[]
     */
    protected byte[] serialize(Object requestBody, Collection<HttpListener> listeners) {
        if (requestBody instanceof String) {
            return ((String) requestBody).getBytes(
                mediaType.getCharset() == null ? StandardCharsets.UTF_8 : mediaType.getCharset());
        } else if (requestBody instanceof byte[]) {
            return (byte[]) requestBody;
        }
        if (listeners.isEmpty()) {
            return serializer.serialize(requestBody);
        }
        byte[] serializeBody = serializer.serialize(requestBody);
        for (HttpListener listener : listeners) {
            listener.onSerialize(requestBody, serializeBody, HttpClientUtils.create(mediaType));
        }
        return serializeBody;
    }

    /**
     * Deserialize.
     *
     * @param <T> the generic type
     * @param entity the response entity
     * @param responseType the response type
     * @param listeners the listeners for this request
     * @return the deserialized object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected <T> T deserialize(HttpEntity entity, Class<T> responseType, Collection<HttpListener> listeners)
        throws IOException {
        Serializer resSerializer = getSerializer(entity);
        byte[] responseBody = EntityUtils.toByteArray(entity);
        if (listeners.isEmpty()) {
            return resSerializer.deserialize(responseBody, responseType);
        }
        T deserializeBody = resSerializer.deserialize(responseBody, responseType);
        for (HttpListener listener : listeners) {
            listener.onDeserialize(responseBody, deserializeBody, HttpClientUtils.create(mediaType));
        }
        return deserializeBody;
    }

    /**
     * Gets the serializer.
     *
     * @param entity the response entity
     * @return the serializer
     */
    private Serializer getSerializer(HttpEntity entity) {
        if (deserializeWithContentType) {
            String contentTypeHeader = entity.getContentType();
            if (contentTypeHeader != null) {
                try {
                    ContentType resContentType = ContentType.parse(contentTypeHeader);
                    Serializer resSerializer = getSerializer(resContentType, false);
                    if (resSerializer != null) {
                        return resSerializer;
                    }
                } catch (RuntimeException e) {
                    logger.warn("invalid content-type {}", contentTypeHeader);
                }
            }
        }
        return serializer;
    }

    /**
     * Gets the serializer.
     *
     * @param contentType the content type
     * @param throwExceptionNoSerializer the throw exception no serializer
     * @return the serializer
     */
    protected Serializer getSerializer(ContentType contentType, boolean throwExceptionNoSerializer) {
        try {
            MimeType mimeType = new MimeType(contentType.getMimeType());
            Serializer mimeTypeSerializer = serialization.getSerializer(mimeType);
            if (mimeTypeSerializer != null) {
                return mimeTypeSerializer;
            }
            if (throwExceptionNoSerializer) {
                throw new SerializationException(
                    SerializationExceptionCode.createNoSerializerForMimeTypeCode(mimeType.getBaseType()));
            }
            logger.warn("no serializer found for content-type {}", mimeType.getBaseType());
        } catch (MimeTypeParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Creates the headers, merging the default headers and the request headers.
     *
     * @param headers the request headers
     * @return the headers
     */
    protected Map<String, String> createHeaders(Map<String, String> headers) {
        Map<String, String> result = new HashMap<>(headersMap);
        if (Lang.isNotEmpty(headers)) {
            result.putAll(headers);
        }
        return result;
    }

    /**
     * Creates the headers and derives the content-type from the request body.
     *
     * @param headers the request headers
     * @param requestObject the request object
     * @return the headers
     */
    protected Map<String, String> createHeaders(Map<String, String> headers, Object requestObject) {
        Map<String, String> result = createHeaders(headers);
        if (requestObject instanceof String) {
            if (!result.containsKey("content-type")) {
                result.put("content-type", textPlain.toString());
            }
        } else if (requestObject instanceof byte[]) {
            result.put("content-type", HttpClientUtils.STREAM_CONTENT_TYPE);
        } else {
            result.put("content-type", mediaType.toString());
        }
        return result;
    }

    /**
     * Gets the content type of the request body.
     *
     * @param requestObject the request object
     * @param headers the headers
     * @return the content type
     */
    protected ContentType getContentType(Object requestObject, Map<String, String> headers) {
        if (requestObject instanceof String) {
            if (headers.containsKey("content-type")) {
                return mediaType;
            }
            return textPlain;
        } else if (requestObject instanceof byte[]) {
            return HttpClientUtils.STREAM_CONTENT_TYPE_OBJ;
        }
        return mediaType;
    }

    /**
     * Gets the charset used to decode textual responses.
     *
     * @return the charset
     */
    protected Charset charset() {
        return mediaType.getCharset() == null ? StandardCharsets.UTF_8 : mediaType.getCharset();
    }

    /**
     * Checks if is success.
     *
     * @param code the status code
     * @return true, if is success
     */
    protected boolean isSuccess(int code) {
        return code == HttpCode.OK || codeSameAsSuccess.contains(code);
    }

    /**
     * Checks if is success (2xx), used by download.
     *
     * @param code the status code
     * @return true, if is success
     */
    protected boolean is2xxSuccess(int code) {
        return code >= 200 && code < 300;
    }

    /**
     * Gets the success response.
     *
     * @param request the request
     * @return the classic response
     */
    protected CloseableHttpResponse getSuccessResponse(ClassicHttpRequest request) {
        long start = System.currentTimeMillis();
        try {
            CloseableHttpResponse response = client.execute(request);
            long networkTime = System.currentTimeMillis() - start;
            if (isSuccess(response.getCode())) {
                return response;
            }
            HttpResponse errorResponse = response(response, networkTime);
            response.close();
            throw new HttpErrorResponseException(Str.format("{0} error, code {1}, message {2}", request.getRequestUri(),
                response.getCode(), response.getReasonPhrase()), errorResponse);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /**
     * Gets the final listeners for a request. Includes the client's global listeners
     * and the listeners configured specifically for this request.
     *
     * @param customListeners the request-level listeners
     * @return the final listeners
     */
    protected Collection<HttpListener> getFinalListeners(Collection<HttpListener> customListeners) {
        ChainSetImpl<HttpListener> finalListeners = new ChainSetImpl<>(new LinkedHashSet<>());
        finalListeners.addAll(listeners);
        if (customListeners != null) {
            finalListeners.addAll(customListeners);
        }
        return finalListeners;
    }

    /**
     * Creates the response adapter from a classic response. The entity is snapshotted so the
     * response can be closed safely.
     *
     * @param response the response
     * @param networkTime the network time
     * @return the http response
     */
    protected HttpResponse response(org.apache.hc.core5.http.ClassicHttpResponse response, long networkTime) {
        byte[] data = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                data = EntityUtils.toByteArray(entity);
            } catch (IOException e) {
                throw new HttpException(e);
            }
        }
        return new ResponseAdapter(response.getCode(), data, HttpClientUtils.headersToMap(response.getHeaders()),
            networkTime);
    }

    /**
     * Creates the response adapter from a simple (async) response.
     *
     * @param response the response
     * @param networkTime the network time
     * @return the http response
     */
    protected HttpResponse response(SimpleHttpResponse response, long networkTime) {
        return new ResponseAdapter(response.getCode(), response.getBodyBytes(),
            HttpClientUtils.headersToMap(response.getHeaders()), networkTime);
    }

    /**
     * Executes the download with the classic client, streaming the entity into the output.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output stream
     * @param progress the progress consumer
     * @return the number of bytes written
     */
    protected long doDownload(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        HttpUriRequestBase request = downloadRequest(httpMethod, url, params, headers);
        long start = System.currentTimeMillis();
        try (CloseableHttpResponse response = client.execute(request)) {
            if (is2xxSuccess(response.getCode())) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return 0L;
                }
                try (InputStream is = entity.getContent()) {
                    byte[] bs = new byte[downloadProgressPerSize];
                    long total = entity.getContentLength();
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
            HttpResponse errorResponse = response(response, System.currentTimeMillis() - start);
            throw new HttpErrorResponseException(Str.format("{0} error, code {1}, message {2}", request.getRequestUri(),
                response.getCode(), response.getReasonPhrase()), errorResponse);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /**
     * Creates the download request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the request
     */
    protected HttpUriRequestBase downloadRequest(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                HttpGet get = new HttpGet(HttpClientUtils.appendParam(url, params));
                HttpClientUtils.applyHeaders(get, createHeaders(headers));
                return get;
            case POST:
                HttpPost post = new HttpPost(url);
                HttpClientUtils.applyHeaders(post, createHeaders(headers));
                post.setEntity(HttpClientUtils.createRequestBody(params));
                return post;
            case PUT:
                HttpPut put = new HttpPut(url);
                HttpClientUtils.applyHeaders(put, createHeaders(headers));
                put.setEntity(HttpClientUtils.createRequestBody(params));
                return put;
            case DELETE:
                HttpDelete delete = new HttpDelete(HttpClientUtils.appendParam(url, params));
                HttpClientUtils.applyHeaders(delete, createHeaders(headers));
                return delete;
            case PATCH:
                org.apache.hc.client5.http.classic.methods.HttpPatch patch = new org.apache.hc.client5.http.classic.methods.HttpPatch(
                    url);
                HttpClientUtils.applyHeaders(patch, createHeaders(headers));
                patch.setEntity(HttpClientUtils.createRequestBody(params));
                return patch;
            default:
                throw new HttpException("download unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * The response adapter.
     */
    private static class ResponseAdapter implements HttpResponse {

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
