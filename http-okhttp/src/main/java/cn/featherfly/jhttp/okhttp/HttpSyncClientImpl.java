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
import cn.featherfly.jhttp.HttpSyncClient;
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
    protected HttpSyncClientImpl(OkHttpClient client, Map<String, String> headers, SerializableStrategy serialization,
        MediaType mediaType) {
        super(client, headers, serialization, mediaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .get().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R get(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .get().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .head().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params)).headers(createHeaders(headers))
            .head().build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .post(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return request(
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
    public String put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .put(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return request(
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
    public String patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        if (requestBody == null) {
            // 不使用请求体，params作为form param
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build());
        } else {
            // 使用请求体，params作为url Query String
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        if (requestBody == null) {
            return request(new Request.Builder().url(url).headers(createHeaders(headers))
                .patch(OkHttpUtils.createRequestBody(params)).build(), responseType);
        } else {
            return request(
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
    public <R> R delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build(), responseType);
        } else {
            return request(
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
    public String delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        // delete form parameter 语义未定义, 直接加入url参数
        if (requestBody == null) {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers)).delete().build());
        } else {
            return request(new Request.Builder().url(OkHttpUtils.appendParam(url, params))
                .headers(createHeaders(headers, requestBody))
                .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                throw new HttpException("http get method can not send request body");
            case POST:
                return stream(new Request.Builder().url(url).headers(createHeaders(headers, requestBody))
                    .post(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
            case PUT:
                return stream(new Request.Builder().url(url).headers(createHeaders(headers, requestBody))
                    .put(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
            case DELETE:
                return stream(new Request.Builder().url(url).headers(createHeaders(headers, requestBody))
                    .delete(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
            case HEAD:
                throw new HttpException("http head method can not send request body");
            case PATCH:
                return stream(new Request.Builder().url(url).headers(createHeaders(headers, requestBody))
                    .patch(RequestBody.create(getMediaType(requestBody, headers), serialize(requestBody))).build());
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
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

    private <R> R request(final Request request, Class<R> responseType) {
        try {
            return deserialize(getSuccessResponse(request), responseType);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private InputStream stream(final Request request) {
        // 需要调用者自行关闭
        return getSuccessResponse(request).body().byteStream();
    }

}
