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
import cn.featherfly.jhttp.HttpRxjavaClient;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.BasicMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.FormMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequest;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HttpRxjavaClientImpl.
 *
 * @author zhongj
 */
public class HttpRxjavaClientImpl extends AbstractHttpClient<HttpRxjavaClientImpl> implements HttpRxjavaClient {

    private boolean autoSubscribeOnIo = true;

    /**
     * Builder.
     *
     * @return the http rxjava client builder
     */
    public static HttpRxjavaClientBuilder builder() {
        return new HttpRxjavaClientBuilder();
    }

    /**
     * Instantiates a new http rxjava client.
     *
     * @param client the client
     * @param headers the headers
     * @param listeners the listeners
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected HttpRxjavaClientImpl(OkHttpClient client, Map<String, String> headers, Set<HttpListener> listeners,
        SerializableStrategy serialization, MediaType mediaType) {
        super(client, headers, listeners, serialization, mediaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoSubscribeOnIo() {
        return autoSubscribeOnIo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoSubscribeOnIo(boolean autoSubscribeOnIo) {
        this.autoSubscribeOnIo = autoSubscribeOnIo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
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
    public Observable<String> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> Observable<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public Observable<String> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build(), responseType, finalListeners);
    }

    @Override
    public Observable<String> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> Observable<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public Observable<String> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build());
    }

    @Override
    public <R> Observable<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build(), responseType, finalListeners);
    }

    @Override
    public <R> Observable<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public Observable<String> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public Observable<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public Observable<String> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public <R> Observable<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                    .build(),
                responseType, finalListeners);
        }
    }

    @Override
    public Observable<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                    .build());
        }
    }

    @Override
    public Observable<String> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public <R> Observable<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build(), responseType, finalListeners);
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    @Override
    public Observable<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public Observable<String> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public Observable<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build());
        }
    }

    @Override
    public <R> Observable<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType, finalListeners);
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody, finalListeners)))
                .build(), responseType, finalListeners);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<Long> download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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

    @Override
    public Observable<Long> download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        final Request request = downloadRequest(httpMethod, url, params, headers);

        Observable<Long> observable = Observable.create(emitter -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    logger.error("error at url: {}", request.url());
                    emitter.onError(new HttpException(e));
                }

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
                            emitter.onNext(readed);
                        }
                    } else {
                        emitter.onError(new HttpErrorResponseException(Str.format("{0} error, code {1}, message {2}",
                            request.url(), response.code(), response.message()), response(response)));
                    }
                }
            });
        });
        if (autoSubscribeOnIo) {
            return observable.subscribeOn(Schedulers.io());
        }
        return observable;
    }

    // ********************************************************************
    //
    // ********************************************************************

    private Observable<String> observation(final Request request) {
        Observable<String> observable = Observable.create(emitter -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    logger.error("error at url: {}", request.url());
                    emitter.onError(new HttpException(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isSuccess(response)) {
                        // body().string() 会自动关闭
                        emitter.onNext(response.body().string());
                    } else {
                        emitter.onError(new HttpErrorResponseException(
                            Str.format("{0} error, code {1}, message {2}", request.url(), response.code(),
                                response.message()),
                            // body().bytes() 会自动关闭
                            response(response)));
                    }
                }
            });
        });
        if (autoSubscribeOnIo) {
            return observable.subscribeOn(Schedulers.io());
        }
        return observable;
    }

    private <R> Observable<R> observation(final Request request, final Class<R> responseType,
        Collection<HttpListener> listeners) {
        Observable<R> observable = Observable.create(emitter -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    logger.error("error at url: {}", request.url());
                    emitter.onError(new HttpException(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isSuccess(response)) {
                        emitter.onNext(deserialize(response, responseType, listeners));
                    } else {
                        emitter.onError(new HttpErrorResponseException(Str.format("{0} error, code {1}, message {2}",
                            request.url(), response.code(), response.message()), response(response)));
                    }
                }
            });
        });
        if (autoSubscribeOnIo) {
            return observable.subscribeOn(Schedulers.io());
        }
        return observable;
    }

}
