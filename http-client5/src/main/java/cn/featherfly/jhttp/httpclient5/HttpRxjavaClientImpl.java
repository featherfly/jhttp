package cn.featherfly.jhttp.httpclient5;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRxjavaClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
     * Instantiates a new http rxjava client from the provider clients created by the builder.
     *
     * @param client the classic client
     * @param asyncClient the async client
     * @param headers the default headers
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpRxjavaClientImpl(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, SerializableStrategy serialization, ContentType contentType) {
        super(client, asyncClient, headers, serialization, contentType);
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
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return observation(builder.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return observation(builder.build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return observation(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return observation(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers), responseType);
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
        return Observable.fromCallable(() -> doDownload(httpMethod, url, params, headers, output, progress))
            .subscribeOn(Schedulers.io());
    }

    private SimpleHttpRequest getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.get(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return builder.build();
    }

    private SimpleHttpRequest entityRequest(Function<String, SimpleRequestBuilder> factory, String url,
        Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        if (requestBody == null) {
            SimpleRequestBuilder builder = factory.apply(url);
            HttpClientUtils.setFormBody(builder, params == null ? new HashMap<>() : params);
            applyHeaders(builder, headers);
            return builder.build();
        }
        SimpleRequestBuilder builder = factory.apply(HttpClientUtils.appendParam(url, params));
        builder.setBody(serialize(requestBody), getContentType(requestBody, safe(headers)));
        applyHeaders(builder, createHeaders(headers, requestBody));
        return builder.build();
    }

    private SimpleHttpRequest deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.delete(HttpClientUtils.appendParam(url, params));
        if (requestBody != null) {
            builder.setBody(serialize(requestBody), getContentType(requestBody, safe(headers)));
            applyHeaders(builder, createHeaders(headers, requestBody));
        } else {
            applyHeaders(builder, headers);
        }
        return builder.build();
    }

    private void applyHeaders(SimpleRequestBuilder builder, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        Map<String, String> all = createHeaders(headers);
        for (Map.Entry<String, String> entry : all.entrySet()) {
            builder.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private Map<String, String> safe(Map<String, String> headers) {
        return headers == null ? new HashMap<>() : headers;
    }

    private String errorMessage(SimpleHttpRequest request, SimpleHttpResponse response) {
        return Str.format("{0} error, code {1}, message {2}", request.getRequestUri(), response.getCode(),
            response.getReasonPhrase());
    }

    private Observable<String> observation(final SimpleHttpRequest request) {
        Observable<String> observable = Observable.create(emitter -> {
            FutureCallback<SimpleHttpResponse> callback = new FutureCallback<SimpleHttpResponse>() {

                @Override
                public void completed(SimpleHttpResponse response) {
                    if (isSuccess(response.getCode())) {
                        byte[] body = response.getBodyBytes();
                        emitter.onNext(new String(body == null ? new byte[0] : body, charset()));
                    } else {
                        emitter.onError(
                            new HttpErrorResponseException(errorMessage(request, response), response(response, 0)));
                    }
                }

                @Override
                public void failed(Exception ex) {
                    logger.error("error at url: {}", request.getRequestUri());
                    emitter.onError(new HttpException(ex));
                }

                @Override
                public void cancelled() {
                    emitter.onError(new HttpException("cancelled"));
                }
            };
            java.util.concurrent.Future<SimpleHttpResponse> future = asyncClient().execute(request, callback);
            emitter.setCancellable(() -> future.cancel(true));
        });
        if (autoSubscribeOnIo) {
            return observable.subscribeOn(Schedulers.io());
        }
        return observable;
    }

    private <R> Observable<R> observation(final SimpleHttpRequest request, final Class<R> responseType) {
        Observable<R> observable = Observable.create(emitter -> {
            FutureCallback<SimpleHttpResponse> callback = new FutureCallback<SimpleHttpResponse>() {

                @Override
                public void completed(SimpleHttpResponse response) {
                    if (isSuccess(response.getCode())) {
                        try {
                            ContentType contentType = response.getContentType();
                            HttpEntity entity = new ByteArrayEntity(response.getBodyBytes(),
                                contentType == null ? ContentType.DEFAULT_BINARY : contentType);
                            emitter.onNext(deserialize(entity, responseType));
                        } catch (IOException e) {
                            emitter.onError(new HttpException(e));
                        }
                    } else {
                        emitter.onError(
                            new HttpErrorResponseException(errorMessage(request, response), response(response, 0)));
                    }
                }

                @Override
                public void failed(Exception ex) {
                    logger.error("error at url: {}", request.getRequestUri());
                    emitter.onError(new HttpException(ex));
                }

                @Override
                public void cancelled() {
                    emitter.onError(new HttpException("cancelled"));
                }
            };
            java.util.concurrent.Future<SimpleHttpResponse> future = asyncClient().execute(request, callback);
            emitter.setCancellable(() -> future.cancel(true));
        });
        if (autoSubscribeOnIo) {
            return observable.subscribeOn(Schedulers.io());
        }
        return observable;
    }
}
