package cn.featherfly.jhttp.jdk;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

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
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * JDK 11 based RxJava client.
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
     * @param client the JDK provider client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpRxjavaClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        Set<HttpListener> listeners, SerializableStrategy serialization, String contentType) {
        super(client, headers, listeners, serialization, contentType);
    }

    @Override
    public boolean isAutoSubscribeOnIo() {
        return autoSubscribeOnIo;
    }

    @Override
    public void setAutoSubscribeOnIo(boolean autoSubscribeOnIo) {
        this.autoSubscribeOnIo = autoSubscribeOnIo;
    }

    @Override
    public <R> Observable<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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
    public Observable<String> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners)).getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> Observable<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners)).getHttpRequest();
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
        return observation(getRequest(url, params, headers));
    }

    @Override
    public <R> Observable<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(getRequest(url, params, headers), responseType, finalListeners);
    }

    @Override
    public Observable<String> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners)).getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> Observable<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners)).getHttpRequest();
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
        return observation(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build());
    }

    @Override
    public <R> Observable<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build(), responseType, finalListeners);
    }

    @Override
    public Observable<String> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners)).getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners)).getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public Observable<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("POST", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> Observable<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("POST", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public Observable<String> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners)).getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners)).getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public Observable<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("PUT", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> Observable<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("PUT", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public Observable<String> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners)).getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners)).getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public Observable<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private Observable<String> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("PATCH", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> Observable<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(entityRequest("PATCH", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public Observable<String> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners)).getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> Observable<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners)).getHttpRequest();
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
        return observation(deleteRequest(url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> Observable<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> Observable<R> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return observation(deleteRequest(url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public Observable<Long> download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        Observable<Long> observable = Observable.fromCallable(
            () -> doDownload(httpMethod, url, params, headers, output, progress));
        return autoSubscribeOnIo ? observable.subscribeOn(Schedulers.io()) : observable;
    }

    @Override
    public Observable<Long> download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
        OutputStream output) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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
    // request building helpers
    // ********************************************************************

    private HttpRequest getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).GET().build();
    }

    private HttpRequest entityRequest(String method, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        if (requestBody == null) {
            JdkHttpUtils.Body body = JdkHttpUtils.createRequestBody(params);
            Map<String, String> allHeaders = createHeaders(headers);
            allHeaders.put("content-type", body.contentType);
            return requestBuilder(url, allHeaders).method(method, BodyPublishers.ofByteArray(body.content)).build();
        }
        return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers, requestBody))
            .method(method, BodyPublishers.ofByteArray(serialize(requestBody, listeners))).build();
    }

    private HttpRequest deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        if (requestBody == null) {
            return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE().build();
        }
        return entityRequest("DELETE", url, requestBody, params, headers, listeners);
    }

    // ********************************************************************
    // execution helpers
    // ********************************************************************

    private Observable<String> observation(final HttpRequest request) {
        Observable<String> observable = Observable.create(emitter -> {
            long start = System.currentTimeMillis();
            CompletableFuture<HttpResponse<byte[]>> future = client.sendAsync(request, BodyHandlers.ofByteArray());
            emitter.setCancellable(() -> future.cancel(true));
            future.whenComplete((response, ex) -> {
                if (ex != null) {
                    emitter.onError(new HttpException(unwrap(ex)));
                } else if (isSuccess(response.statusCode())) {
                    Charset charset = JdkHttpUtils.charsetOf(response.headers().firstValue("content-type").orElse(null));
                    emitter.onNext(new String(response.body(), charset));
                } else {
                    emitter.onError(new HttpErrorResponseException(errorMessage(request, response.statusCode()),
                        response(response, System.currentTimeMillis() - start)));
                }
            });
        });
        return autoSubscribeOnIo ? observable.subscribeOn(Schedulers.io()) : observable;
    }

    private <R> Observable<R> observation(final HttpRequest request, final Class<R> responseType,
        Collection<HttpListener> listeners) {
        Observable<R> observable = Observable.create(emitter -> {
            long start = System.currentTimeMillis();
            CompletableFuture<HttpResponse<byte[]>> future = client.sendAsync(request, BodyHandlers.ofByteArray());
            emitter.setCancellable(() -> future.cancel(true));
            future.whenComplete((response, ex) -> {
                if (ex != null) {
                    emitter.onError(new HttpException(unwrap(ex)));
                } else if (isSuccess(response.statusCode())) {
                    try {
                        emitter.onNext(deserialize(response.body(),
                            response.headers().firstValue("content-type").orElse(null), responseType, listeners));
                    } catch (RuntimeException e) {
                        emitter.onError(new HttpException(e));
                    }
                } else {
                    emitter.onError(new HttpErrorResponseException(errorMessage(request, response.statusCode()),
                        response(response, System.currentTimeMillis() - start)));
                }
            });
        });
        return autoSubscribeOnIo ? observable.subscribeOn(Schedulers.io()) : observable;
    }

    private static Throwable unwrap(Throwable ex) {
        if (ex instanceof CompletionException && ex.getCause() != null) {
            return ex.getCause();
        }
        return ex;
    }
}
