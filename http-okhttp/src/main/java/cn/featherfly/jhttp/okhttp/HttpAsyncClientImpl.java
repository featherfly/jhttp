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
import cn.featherfly.jhttp.HttpAsyncClient;
import cn.featherfly.jhttp.HttpErrorResponse;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRequestCompletion;
import cn.featherfly.jhttp.HttpRequestCompletionImpl;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.BasicMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.FormMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequest;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http async client impl.
 *
 * @author zhongj
 */
public class HttpAsyncClientImpl extends AbstractHttpClient<HttpAsyncClientImpl> implements HttpAsyncClient {

    /**
     * Builder.
     *
     * @return the http async client builder
     */
    public static HttpAsyncClientBuilder builder() {
        return new HttpAsyncClientBuilder();
    }

    /**
     * Instantiates a new http async client.
     *
     * @param client the client
     * @param headers the headers
     * @param listeners the listeners
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected HttpAsyncClientImpl(OkHttpClient client, Map<String, String> headers, Set<HttpListener> listeners,
        SerializableStrategy serialization, MediaType mediaType) {
        super(client, headers, listeners, serialization, mediaType);
    }

    @Override
    public <R> HttpRequestCompletion<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
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
                return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    responseType, request.getListeners());
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

    @Override
    public HttpRequestCompletion<String> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
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
                return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
                    request.getListeners());
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
    public HttpRequestCompletion<String> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> get0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build());
    }

    @Override
    public <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build(), responseType, listeners);
    }

    @Override
    public HttpRequestCompletion<String> head(
        Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> head0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> head0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build(), responseType, listeners);
    }

    @Override
    public HttpRequestCompletion<String> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    public <R> HttpRequestCompletion<R> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public HttpRequestCompletion<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    public HttpRequestCompletion<String> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public <R> HttpRequestCompletion<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createFormBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createFormBody(params)).build(), responseType, finalListeners);
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public HttpRequestCompletion<String> patch(
        Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createFormBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createFormBody(params)).build(), responseType, finalListeners);
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public <R> HttpRequestCompletion<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> delete(
        Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType, finalListeners);
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public HttpRequestCompletion<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<Long> download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        final Request request = downloadRequest(httpMethod, url, params, headers);

        HttpRequestCompletionImpl<Long> completion = new HttpRequestCompletionImpl<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
                        completion.setResponse(readed);
                    }
                } else {
                    completion.setHttpErrorResponse(new HttpErrorResponse(Str.format("{0} error, code {1}, message {2}",
                        request.url(), response.code(), response.message()), response(response)));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                completion.setHttpErrorResponse(new HttpErrorResponse(e.getMessage()));
            }
        });
        return completion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<Long> download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
        OutputStream output) {
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

    // ********************************************************************
    // private method
    // ********************************************************************

    private HttpRequestCompletion<String> completetion(final Request request) {
        HttpRequestCompletionImpl<String> completion = new HttpRequestCompletionImpl<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isSuccess(response)) {
                    // body().string() 会自动关闭
                    completion.setResponse(response.body().string());
                } else {
                    completion.setHttpErrorResponse(new HttpErrorResponse(
                        Str.format("{0} error, code {1}, message {2}", request.url(), response.code(),
                            response.message()),
                        // body().bytes() 会自动关闭
                        response(response)));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                completion.setHttpErrorResponse(new HttpErrorResponse(e.getMessage()));
                completion.completeExceptionally(new HttpException(e));
            }
        });
        return completion;
    }

    private <R> HttpRequestCompletion<R> completetion(final Request request, final Class<R> responseType,
        Collection<HttpListener> listeners) {
        HttpRequestCompletionImpl<R> completion = new HttpRequestCompletionImpl<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (isSuccess(response)) {
                    completion.setResponse(deserialize(response, responseType, listeners));
                } else {
                    completion.setHttpErrorResponse(new HttpErrorResponse(Str.format("{0} error, code {1}, message {2}",
                        request.url(), response.code(), response.message()), response(response)));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                completion.setHttpErrorResponse(new HttpErrorResponse(e.getMessage()));
                completion.completeExceptionally(new HttpException(e));
            }
        });
        return completion;
    }
}
