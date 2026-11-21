package cn.featherfly.jhttp.jdk;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRxjavaClient;
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
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpRxjavaClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        SerializableStrategy serialization, String contentType) {
        super(client, headers, serialization, contentType);
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
    public Observable<String> get(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return observation(getRequest(url, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return observation(getRequest(url, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return observation(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).method("HEAD", BodyPublishers.noBody()).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return observation(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).method("HEAD", BodyPublishers.noBody()).build(),
            responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest("POST", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest("POST", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest("PUT", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest("PUT", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest("PATCH", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest("PATCH", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(deleteRequest(url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(deleteRequest(url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<Long> download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        Observable<Long> observable = Observable.fromCallable(
            () -> doDownload(httpMethod, url, params, headers, output, progress));
        return autoSubscribeOnIo ? observable.subscribeOn(Schedulers.io()) : observable;
    }

    private HttpRequest getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).GET().build();
    }

    private HttpRequest entityRequest(String method, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            JdkHttpUtils.Body body = JdkHttpUtils.createRequestBody(params);
            Map<String, String> allHeaders = createHeaders(headers);
            allHeaders.put("content-type", body.contentType);
            return requestBuilder(url, allHeaders).method(method, BodyPublishers.ofByteArray(body.content)).build();
        }
        return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers, requestBody)).method(method,
            BodyPublishers.ofByteArray(serialize(requestBody))).build();
    }

    private HttpRequest deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE().build();
        }
        return entityRequest("DELETE", url, requestBody, params, headers);
    }

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

    private <R> Observable<R> observation(final HttpRequest request, final Class<R> responseType) {
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
                            response.headers().firstValue("content-type").orElse(null), responseType));
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
