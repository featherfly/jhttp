package cn.featherfly.jhttp.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRxjavaClient;
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
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected HttpRxjavaClientImpl(OkHttpClient client, Map<String, String> headers, SerializableStrategy serialization,
        MediaType mediaType) {
        super(client, headers, serialization, mediaType);
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
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
                    .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Observable<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return observation(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType);
        } else {
            return observation(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                    .headers(createHeaders(headers, requestBody))
                    .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    private <R> Observable<R> observation(final Request request, final Class<R> responseType) {
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
                        emitter.onNext(deserialize(response, responseType));
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
