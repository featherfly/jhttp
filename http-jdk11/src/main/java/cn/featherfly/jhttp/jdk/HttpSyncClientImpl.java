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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpErrorResponseException;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpSyncClient;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.BasicMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.FormMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

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
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpSyncClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        Set<HttpListener> listeners, SerializableStrategy serialization, String contentType) {
        super(client, headers, listeners, serialization, contentType);
    }

    @Override
    public String request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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

    @Override
    public String get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> R get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public String get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, getFinalListeners(listeners));
    }

    private String get0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(getRequest(url, params, headers));
    }

    @Override
    public <R> R get(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R get0(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> finalListeners) {
        return request(getRequest(url, params, headers), responseType, finalListeners);
    }

    @Override
    public String head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> R head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public String head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, getFinalListeners(listeners));
    }

    private String head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build());
    }

    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build(), responseType, finalListeners);
    }

    @Override
    public String post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return request(entityRequest("POST", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest("POST", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public String put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String put0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(entityRequest("PUT", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R put0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest("PUT", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public String patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return request(entityRequest("PATCH", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest("PATCH", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public String delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return request(deleteRequest(url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(deleteRequest(url, requestBody, params, headers, finalListeners), responseType, finalListeners);
    }

    @Override
    public Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        return doDownload(httpMethod, url, params, headers, output, progress);
    }

    @Override
    public Long download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder, OutputStream output) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
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
    public InputStream stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return stream0(httpMethod, url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private InputStream stream0(HttpMethod httpMethod, String url, Object requestBody,
        Map<String, Serializable> params, Map<String, String> headers, Collection<HttpListener> finalListeners) {
        if (requestBody == null) {
            switch (httpMethod) {
                case GET:
                    return stream(getRequest(url, params, headers));
                case HEAD:
                    return stream(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
                        .method("HEAD", BodyPublishers.noBody()).build());
                case DELETE:
                    return stream(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
                        .DELETE().build());
                case POST:
                case PUT:
                case PATCH: {
                    JdkHttpUtils.Body body = JdkHttpUtils.createRequestBody(params);
                    Map<String, String> allHeaders = createHeaders(headers);
                    allHeaders.put("content-type", body.contentType);
                    return stream(requestBuilder(url, allHeaders)
                        .method(httpMethod.name(), BodyPublishers.ofByteArray(body.content)).build());
                }
                default:
                    throw new HttpException("unsupport http method " + httpMethod.toString());
            }
        }
        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.HEAD) {
            throw new HttpException("http " + httpMethod.toString().toLowerCase()
                + " method can not send request body");
        }
        return stream(requestBuilder(url, createHeaders(headers, requestBody))
            .method(httpMethod.name(), BodyPublishers.ofByteArray(serialize(requestBody, finalListeners))).build());
    }

    @Override
    public InputStream stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return stream0(HttpMethod.GET, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case HEAD:
                return stream0(HttpMethod.HEAD, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case DELETE:
                return stream0(HttpMethod.DELETE, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case POST:
                return stream0(HttpMethod.POST, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case PUT:
                return stream0(HttpMethod.PUT, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case PATCH:
                return stream0(HttpMethod.PATCH, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
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

    private String request(HttpRequest request) {
        HttpResponse<byte[]> response = getSuccessResponse(request);
        Charset charset = JdkHttpUtils.charsetOf(response.headers().firstValue("content-type").orElse(null));
        return new String(response.body(), charset);
    }

    private <R> R request(HttpRequest request, Class<R> responseType, Collection<HttpListener> listeners) {
        HttpResponse<byte[]> response = getSuccessResponse(request);
        return deserialize(response.body(), response.headers().firstValue("content-type").orElse(null), responseType,
            listeners);
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
