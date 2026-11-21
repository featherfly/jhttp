package cn.featherfly.jhttp.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import cn.featherfly.common.lang.Str;
import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpAsyncClient;
import cn.featherfly.jhttp.HttpErrorResponse;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRequestCompletion;
import cn.featherfly.jhttp.HttpRequestCompletionImpl;
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
     * @param serialization the serialization
     * @param mediaType the media type
     */
    protected HttpAsyncClientImpl(OkHttpClient client, Map<String, String> headers, SerializableStrategy serialization,
        MediaType mediaType) {
        super(client, headers, serialization, mediaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> get(String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).get().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> head(String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
            .headers(createHeaders(headers)).head().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return completetion(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                    .headers(createHeaders(headers, requestBody))
                    .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createFormBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createFormBody(params)).build(), responseType);
        } else {
            return completetion(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                    .headers(createHeaders(headers, requestBody))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createFormBody(params)).build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        if (requestBody == null) {
            return completetion(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createFormBody(params)).build(), responseType);
        } else {
            return completetion(
                new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                    .headers(createHeaders(headers, requestBody))
                    .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build(),
                responseType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType);
        } else {
            return completetion(
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
    public HttpRequestCompletion<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return completetion(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
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

    private <R> HttpRequestCompletion<R> completetion(final Request request, final Class<R> responseType) {
        HttpRequestCompletionImpl<R> completion = new HttpRequestCompletionImpl<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (isSuccess(response)) {
                    completion.setResponse(deserialize(response, responseType));
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
