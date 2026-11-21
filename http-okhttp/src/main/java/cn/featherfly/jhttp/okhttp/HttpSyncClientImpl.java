package cn.featherfly.jhttp.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpSyncClient;
import cn.featherfly.jhttp.HttpUtils;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.BasicMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.FormMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequest;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HttpSyncClientImpl.
 *
 * @author zhongj
 */
public class HttpSyncClientImpl extends AbstractHttpClient<HttpSyncClientImpl> implements HttpSyncClient {

    /**
     * Builder.
     *
     * @return the http sync client builder
     */
    public static HttpSyncClientBuilder builder() {
        return new HttpSyncClientBuilder();
    }

    /**
     * Instantiates a new http client.
     *
     * @param client the client
     * @param headers the headers
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected HttpSyncClientImpl(OkHttpClient client, Map<String, String> headers, Set<HttpListener> listeners,
        SerializableStrategy serialization, MediaType mediaType) {
        super(client, headers, listeners, serialization, mediaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, getFinalListeners(listeners));
    }

    private String get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .get().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R get(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R get0(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> finalListeners) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .get().build(), responseType, finalListeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, getFinalListeners(listeners));
    }

    private String head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .head().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .head().build(), responseType, finalListeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String post0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R post0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String put0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R put0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String patch0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R patch0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R delete0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType, finalListeners);
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, listeners)))
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        Request request = downloadRequest(httpMethod, url, params, headers);
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                try (InputStream is = response.body().byteStream()) {
                    byte[] bs = new byte[downloadProgressPerSize];
                    long total = response.body().contentLength();
                    int len = -1;
                    long readed = 0;
                    while ((len = is.read(bs)) != -1) {
                        readed += len;
                        progress.accept(readed, total);
                        output.write(bs, 0, len);
                    }
                    return readed;
                }
            } else {
                throw new HttpErrorResponseException(
                    Str.format("{0} error, code {1}, message {2}", request.url(), response.code(), response.message()),
                    response(response));
            }
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public Long download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder, OutputStream output) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return download(HttpMethod.GET, request.getUrl(), request.getParameters(), request.getHeaders(),
                    output);
            case POST:
                return download(HttpMethod.POST, request.getUrl(), request.getParameters(), request.getHeaders(),
                    output);
            case PUT:
                return download(HttpMethod.PUT, request.getUrl(), request.getParameters(), request.getHeaders(),
                    output);
            case DELETE:
                return download(HttpMethod.DELETE, request.getUrl(), request.getParameters(), request.getHeaders(),
                    output);
            case PATCH:
                return download(HttpMethod.PATCH, request.getUrl(), request.getParameters(), request.getHeaders(),
                    output);
            default:
                throw new HttpException("unsupport http method " + request.getMethod().toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return stream0(httpMethod, url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private InputStream stream0(HttpMethod httpMethod, String url, Object requestBody,
        Map<String, Serializable> params, Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            switch (httpMethod) {
                case GET:
                    return stream(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers)).get().build());
                case POST:
                    return stream(new Request.Builder().url(url).headers(createHeaders(headers))
                        .post(OkHttpUtils.createRequestBody(params)).build());
                case PUT:
                    return stream(new Request.Builder().url(url).headers(createHeaders(headers))
                        .put(OkHttpUtils.createRequestBody(params)).build());
                case DELETE:
                    return stream(new Request.Builder().url(url).headers(createHeaders(headers)).delete().build());
                case HEAD:
                    return stream(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers)).head().build());
                case PATCH:
                    return stream(new Request.Builder().url(url).headers(createHeaders(headers))
                        .patch(OkHttpUtils.createFormBody(params)).build());
                default:
                    throw new HttpException("unsupport http method " + httpMethod.toString());
            }
        } else {
            switch (httpMethod) {
                case GET:
                    throw new HttpException("http get method can not send request body");
                case POST:
                    return stream(new Request.Builder().url(HttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers, requestBody))
                        .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                        .build());
                case PUT:
                    return stream(new Request.Builder().url(HttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers, requestBody))
                        .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                        .build());
                case DELETE:
                    return stream(new Request.Builder().url(HttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers, requestBody))
                        .delete(
                            RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                        .build());
                case HEAD:
                    throw new HttpException("http head method can not send request body");
                case PATCH:
                    return stream(new Request.Builder().url(HttpUtils.appendParam(url, params))
                        .headers(createHeaders(headers, requestBody))
                        .patch(
                            RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                        .build());
                default:
                    throw new HttpException("unsupport http method " + httpMethod.toString());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return stream0(HttpMethod.GET, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case POST:
                return stream0(HttpMethod.POST, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case PUT:
                return stream0(HttpMethod.PUT, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case DELETE:
                return stream0(HttpMethod.DELETE, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case PATCH:
                return stream0(HttpMethod.PATCH, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            default:
                throw new HttpException("unsupport http method " + request.getMethod().toString());
        }
    }

    @Override
    public String request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
            case POST:
                return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case PUT:
                return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case DELETE:
                return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case HEAD:
                return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
            case PATCH:
                return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    request.getListeners());
            default:
                throw new HttpException("unsupport http method " + request.getMethod().toString());
        }
    }

    @Override
    public <R> R request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
                    request.getListeners());
            case POST:
                return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    responseType, request.getListeners());
            case PUT:
                return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    responseType, request.getListeners());
            case DELETE:
                return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), responseType, request.getListeners());
            case HEAD:
                return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
                    request.getListeners());
            case PATCH:
                return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    responseType, request.getListeners());
            default:
                throw new HttpException("unsupport http method " + request.getMethod().toString());
        }
    }

    // ********************************************************************
    //
    // ********************************************************************

    private String request(final Request request) {
        try {
            // body().string() 会自动关闭
            return getSuccessResponse(request).body().string();
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private <R> R request(final Request request, Class<R> responseType, Collection<HttpListener> listeners) {
        try {
            return deserialize(getSuccessResponse(request), responseType, listeners);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private InputStream stream(final Request request) {
        // 需要调用者自行关闭
        return getSuccessResponse(request).body().byteStream();
    }

}
