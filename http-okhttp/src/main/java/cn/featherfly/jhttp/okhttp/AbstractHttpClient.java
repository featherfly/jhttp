package cn.featherfly.jhttp.okhttp;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

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
import cn.featherfly.jhttp.HttpResponse;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * abstract http client.
 *
 * @author zhongj
 * @param <C> the generic type
 */
public abstract class AbstractHttpClient<C extends AbstractHttpClient<C>> {

    /** The logger. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The client. */
    protected OkHttpClient client;

    /** The deserialize with content type. */
    protected boolean deserializeWithContentType;

    /** The serialization. */
    protected SerializableStrategy serialization;

    private Serializer serializer;

    private MediaType mediaType;

    /** The text plain. */
    protected MediaType textPlain;

    /** The headers. */
    protected Headers headers;

    /** The headers map. */
    protected Map<String, String> headersMap = new HashMap<>();

    private Set<Integer> codeSameAsSuccess = new HashSet<>();

    /** The download progress per size. */
    protected int downloadProgressPerSize = 1024 * 256;

    /** The listeners. */
    protected final Set<HttpListener> listeners = new LinkedHashSet<>(0);

    /**
     * Instantiates a new http client.
     *
     * @param client the client
     * @param headers the headers
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected AbstractHttpClient(OkHttpClient client, Map<String, String> headers, SerializableStrategy serialization,
        MediaType mediaType) {
        init(client, headers, serialization, mediaType);
    }

    /**
     * Inits the.
     *
     * @param client the client
     * @param headers the headers
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected void init(OkHttpClient client, Map<String, String> headers, SerializableStrategy serialization,
        MediaType mediaType) {
        AssertIllegalArgument.isNotNull(client, "client");
        AssertIllegalArgument.isNotNull(serialization, "serialization");
        AssertIllegalArgument.isNotNull(mediaType, "mediaType");

        this.client = client;
        this.serialization = serialization;
        this.mediaType = mediaType;

        if (this.mediaType.charset() != null) {
            textPlain = MediaType.parse("text/plain; charset=" + this.mediaType.charset());
            this.serialization.setCharset(this.mediaType.charset());
        } else {
            textPlain = MediaType.parse("text/plain;");
        }

        serializer = getSerializer(this.mediaType, true);

        if (headers != null) {
            headersMap.putAll(headers);
        }
        this.headers = OkHttpUtils.createHeaders(headersMap);
        //        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        client.dispatcher().executorService().shutdown();
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
     * serialize.
     *
     * @param requestBody the request body
     * @return the serialized byte[]
     */
    protected byte[] serialize(Object requestBody) {
        if (requestBody instanceof String) {
            return ((String) requestBody).getBytes(mediaType.charset());
        } else if (requestBody instanceof byte[]) {
            return (byte[]) requestBody;
        }
        if (listeners.isEmpty()) {
            return serializer.serialize(requestBody);
        }
        byte[] serializeBody = serializer.serialize(requestBody);
        for (HttpListener listener : listeners) {
            listener.onSerialize(requestBody, serializeBody, OkHttpUtils.create(mediaType));
        }
        return serializeBody;
    }

    /**
     * Deserialize.
     *
     * @param <T> the generic type
     * @param response the response
     * @param responseType the response type
     * @return the deserialized object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected <T> T deserialize(Response response, final Class<T> responseType) throws IOException {
        Serializer resSerializer = getSerializer(response);
        if (listeners.isEmpty()) {
            // body().bytes() 会自动关闭
            return resSerializer.deserialize(response.body().bytes(), responseType);
        }
        byte[] responseBody = response.body().bytes();
        T deserializeBody = resSerializer.deserialize(responseBody, responseType);
        for (HttpListener listener : listeners) {
            listener.onDeserialize(responseBody, deserializeBody, OkHttpUtils.create(mediaType));
        }
        return deserializeBody;
    }

    /**
     * Gets the serializer.
     *
     * @param response the response
     * @return the serializer
     */
    private Serializer getSerializer(Response response) {
        if (deserializeWithContentType) {
            Serializer resSerializer = null;
            MediaType resMediaType;
            resMediaType = response.body().contentType();
            if (resMediaType == null) {
                //                resMediaType = HttpUtils.JSON_MEDIA_TYPE;
                resMediaType = mediaType;
            }
            resSerializer = getSerializer(resMediaType, false);
            if (resSerializer != null) {
                return resSerializer;
            }
        }
        return serializer;
    }

    /**
     * Gets the serializer.
     *
     * @param mediaType the media type
     * @param throwExceptionNoSerializer the throw exception no serializer
     * @return the serializer
     */
    protected Serializer getSerializer(MediaType mediaType, boolean throwExceptionNoSerializer) {
        try {
            MimeType mimeType = new MimeType(mediaType.type(), mediaType.subtype());
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
     * Creates the headers.
     *
     * @param headers the headers
     * @return the headers
     */
    protected Headers createHeaders(Map<String, String> headers) {
        if (Lang.isEmpty(headers)) {
            return this.headers;
        } else {
            return OkHttpUtils.createHeaders(headers, headersMap);
        }
    }

    /**
     * Creates the headers.
     *
     * @param headers the headers
     * @param requestObject the request object
     * @return the headers
     */
    protected Headers createHeaders(Map<String, String> headers, Object requestObject) {
        if (Lang.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        if (requestObject instanceof String) {
            addContentType(headers, textPlain.toString(), false);
        } else if (requestObject instanceof byte[]) {
            addContentType(headers, OkHttpUtils.STREAM_CONTENT_TYPE, true);
        } else {
            addContentType(headers, mediaType.toString(), true);
        }
        return createHeaders(headers);
    }

    /**
     * Gets the media type.
     *
     * @param requestObject the request object
     * @param headers the headers
     * @return the media type
     */
    protected MediaType getMediaType(Object requestObject, Map<String, String> headers) {
        if (requestObject instanceof String) {
            if (headers.containsKey("content-type")) {
                return mediaType;
            } else {
                return textPlain;
            }
        } else if (requestObject instanceof byte[]) {
            return OkHttpUtils.STREAM_MEDIA_TYPE;
        } else {
            return mediaType;
        }
    }

    private void addContentType(Map<String, String> headers, String value, boolean replace) {
        if (replace) {
            headers.put("content-type", value);
        } else {
            if (!headers.containsKey("content-type")) {
                headers.put("content-type", value);
            }
        }
    }

    /**
     * Checks if is success.
     *
     * @param response the response
     * @return true, if is success
     */
    protected boolean isSuccess(Response response) {
        return response.code() == HttpCode.OK || isCodeSameAsSuccess(response);
    }

    /**
     * Checks if is code same as success.
     *
     * @param response the response
     * @return true, if is code same as success
     */
    protected boolean isCodeSameAsSuccess(Response response) {
        return codeSameAsSuccess.contains(response.code());
    }

    /**
     * Gets the success response.
     *
     * @param request the request
     * @return the success response
     */
    protected Response getSuccessResponse(final Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (isSuccess(response)) {
                return response;
            } else {
                throw new HttpErrorResponseException(
                    Str.format("{0} error, code {1}, message {2}", request.url(), response.code(), response.message()),
                    response(response));
            }
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /**
     * On.
     *
     * @param listener the listener
     * @return the c
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
     * @return the c
     */
    @SuppressWarnings("unchecked")
    public C un(HttpListener listener) {
        listeners.remove(listener);
        return (C) this;
    }

    /**
     * Response.
     *
     * @param response the response
     * @return the http response
     */
    protected HttpResponse response(Response response) {
        return new HttpResponse() {

            @Override
            public int getStatusCode() {
                return response.code();
            }

            @Override
            public long getNetworkTime() {
                return response.receivedResponseAtMillis() - response.sentRequestAtMillis();
            }

            @Override
            public Map<String, String> getHeaders() {
                return OkHttpUtils.headersToMap(response.headers());
            }

            @Override
            public byte[] getData() {
                try {
                    return response.body().bytes();
                } catch (IOException e) {
                    throw new HttpException(e);
                }
            }
        };
    }

    protected Request downloadRequest(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                return new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .get().build();
            case POST:
                return new Request.Builder().url(url).headers(createHeaders(headers))
                    .post(OkHttpUtils.createRequestBody(params)).build();
            case PUT:
                return new Request.Builder().url(url).headers(createHeaders(headers))
                    .put(OkHttpUtils.createRequestBody(params)).build();
            case DELETE:
                return new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .delete().build();
            case PATCH:
                return new Request.Builder().url(url).headers(createHeaders(headers))
                    .patch(OkHttpUtils.createRequestBody(params)).build();
            default:
                throw new HttpException("download unsupport http method " + httpMethod.toString());
        }
    }

}
