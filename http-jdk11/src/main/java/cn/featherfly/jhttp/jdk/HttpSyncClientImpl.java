package cn.featherfly.jhttp.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Map;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpSyncClient;

/**
 * JDK 11 based synchronous client.
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
     * Instantiates a new http sync client.
     *
     * @param client the JDK provider client
     * @param headers the default headers
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpSyncClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        SerializableStrategy serialization, String contentType) {
        super(client, headers, serialization, contentType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return request(getRequest(url, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R get(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType) {
        return request(getRequest(url, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).method("HEAD", BodyPublishers.noBody()).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType) {
        return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).method("HEAD", BodyPublishers.noBody()).build(),
            responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest("POST", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest("POST", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest("PUT", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest("PUT", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest("PATCH", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest("PATCH", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE()
                .build());
        }
        return request(entityRequest("DELETE", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        if (requestBody == null) {
            return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE()
                .build(), responseType);
        }
        return request(entityRequest("DELETE", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        return doDownload(httpMethod, url, params, headers, output, progress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                return stream(getRequest(url, params, headers));
            case HEAD:
                return stream(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).method("HEAD", BodyPublishers.noBody())
                .build());
            case DELETE:
                return stream(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE()
                    .build());
            case POST:
            case PUT:
            case PATCH: {
                JdkHttpUtils.Body body = JdkHttpUtils.createRequestBody(params);
                Map<String, String> allHeaders = createHeaders(headers);
                allHeaders.put("content-type", body.contentType);
                return stream(requestBuilder(url, allHeaders).method(httpMethod.name(),
                    BodyPublishers.ofByteArray(body.content)).build());
            }
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers) {
        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.HEAD) {
            throw new HttpException("http " + httpMethod.toString().toLowerCase()
                + " method can not send request body");
        }
        Map<String, String> allHeaders = createHeaders(headers, requestBody);
        HttpRequest.Builder builder = requestBuilder(url, allHeaders);
        return stream(builder.method(httpMethod.name(), BodyPublishers.ofByteArray(serialize(requestBody))).build());
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

    private String request(HttpRequest request) {
        HttpResponse<byte[]> response = getSuccessResponse(request);
        Charset charset = JdkHttpUtils.charsetOf(response.headers().firstValue("content-type").orElse(null));
        return new String(response.body(), charset);
    }

    private <R> R request(HttpRequest request, Class<R> responseType) {
        HttpResponse<byte[]> response = getSuccessResponse(request);
        return deserialize(response.body(), response.headers().firstValue("content-type").orElse(null), responseType);
    }

    private InputStream stream(HttpRequest request) {
        try {
            HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
            if (is2xxSuccess(response.statusCode())) {
                return response.body();
            }
            byte[] errorBody;
            try (InputStream is = response.body()) {
                errorBody = is.readAllBytes();
            }
            throw new HttpErrorResponseException(errorMessage(request, response.statusCode()),
                response(response.statusCode(), errorBody, JdkHttpUtils.headersToMap(response.headers().map()), 0));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new HttpException(e);
        }
    }
}
