package cn.featherfly.jhttp.jdk;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

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
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

/**
 * JDK 11 based asynchronous client.
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
     * @param client the JDK provider client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpAsyncClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        Set<HttpListener> listeners, SerializableStrategy serialization, String contentType) {
        super(client, headers, listeners, serialization, contentType);
    }

    @Override
    public <R> HttpRequestCompletion<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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
    public HttpRequestCompletion<String> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
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
    public HttpRequestCompletion<String> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners)).getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners)).getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> get(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return get0(url, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> get0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completion(getRequest(url, params, headers));
    }

    @Override
    public <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return get0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> get0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(getRequest(url, params, headers), responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners)).getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners)).getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), responseType,
            request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> head(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return head0(url, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> head0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completion(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> head0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build(), responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners)).getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners)).getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completion(entityRequest("POST", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest("POST", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners)).getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners)).getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completion(entityRequest("PUT", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest("PUT", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners)).getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners)).getHttpRequest();
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
        return completion(entityRequest("PATCH", url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest("PATCH", url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners)).getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        cn.featherfly.jhttp.request.HttpRequest request = requestBuilder
            .apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners)).getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public HttpRequestCompletion<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private HttpRequestCompletion<String> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> finalListeners) {
        return completion(deleteRequest(url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return delete0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> delete0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(deleteRequest(url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public HttpRequestCompletion<Long> download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        HttpRequestCompletionImpl<Long> completion = new HttpRequestCompletionImpl<>();
        CompletableFuture.supplyAsync(() -> doDownload(httpMethod, url, params, headers, output, progress))
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    completion.setResponse(result);
                } else {
                    completion.setHttpErrorResponse(new HttpErrorResponse(unwrap(ex).getMessage()));
                }
            });
        return completion;
    }

    @Override
    public HttpRequestCompletion<Long> download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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

    private HttpRequestCompletion<String> completion(final HttpRequest request) {
        HttpRequestCompletionImpl<String> completion = new HttpRequestCompletionImpl<>();
        long start = System.currentTimeMillis();
        client.sendAsync(request, BodyHandlers.ofByteArray()).whenComplete((response, ex) -> {
            long networkTime = System.currentTimeMillis() - start;
            if (ex != null) {
                Throwable cause = unwrap(ex);
                completion.setHttpErrorResponse(new HttpErrorResponse(cause.getMessage()));
                completion.completeExceptionally(new HttpException(cause));
            } else if (isSuccess(response.statusCode())) {
                Charset charset = JdkHttpUtils.charsetOf(response.headers().firstValue("content-type").orElse(null));
                completion.setResponse(new String(response.body(), charset));
            } else {
                completion.setHttpErrorResponse(new HttpErrorResponse(errorMessage(request, response.statusCode()),
                    response(response, networkTime)));
            }
        });
        return completion;
    }

    private <R> HttpRequestCompletion<R> completion(final HttpRequest request, final Class<R> responseType,
        Collection<HttpListener> listeners) {
        HttpRequestCompletionImpl<R> completion = new HttpRequestCompletionImpl<>();
        long start = System.currentTimeMillis();
        client.sendAsync(request, BodyHandlers.ofByteArray()).whenComplete((response, ex) -> {
            long networkTime = System.currentTimeMillis() - start;
            if (ex != null) {
                Throwable cause = unwrap(ex);
                completion.setHttpErrorResponse(new HttpErrorResponse(cause.getMessage()));
                completion.completeExceptionally(new HttpException(cause));
            } else if (isSuccess(response.statusCode())) {
                try {
                    completion.setResponse(deserialize(response.body(),
                        response.headers().firstValue("content-type").orElse(null), responseType, listeners));
                } catch (RuntimeException e) {
                    completion.completeExceptionally(new HttpException(e));
                }
            } else {
                completion.setHttpErrorResponse(new HttpErrorResponse(errorMessage(request, response.statusCode()),
                    response(response, networkTime)));
            }
        });
        return completion;
    }

    private static Throwable unwrap(Throwable ex) {
        if (ex instanceof CompletionException && ex.getCause() != null) {
            return ex.getCause();
        }
        return ex;
    }
}
