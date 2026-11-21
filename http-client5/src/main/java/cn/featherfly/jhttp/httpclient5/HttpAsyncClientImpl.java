package cn.featherfly.jhttp.httpclient5;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import cn.featherfly.jhttp.request.HttpRequest;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

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
     * Instantiates a new http async client from the provider clients created by the builder.
     *
     * @param client the classic client
     * @param asyncClient the async client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpAsyncClientImpl(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, Set<HttpListener> listeners, SerializableStrategy serialization,
        ContentType contentType) {
        super(client, asyncClient, headers, listeners, serialization, contentType);
    }

    @Override
    public <R> HttpRequestCompletion<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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

    @Override
    public HttpRequestCompletion<String> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
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
    public HttpRequestCompletion<String> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
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
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
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
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return completion(builder.build());
    }

    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> head0(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return completion(builder.build(), responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
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
        return completion(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> post0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers, finalListeners),
            responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
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
        return completion(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> put0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers, finalListeners),
            responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
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
        return completion(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> HttpRequestCompletion<R> patch0(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> finalListeners) {
        return completion(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers, finalListeners),
            responseType, finalListeners);
    }

    @Override
    public HttpRequestCompletion<String> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> HttpRequestCompletion<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
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
        java.util.concurrent.CompletableFuture
            .supplyAsync(() -> doDownload(httpMethod, url, params, headers, output, progress))
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    completion.setResponse(result);
                } else {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    completion.setHttpErrorResponse(new HttpErrorResponse(cause.getMessage()));
                }
            });
        return completion;
    }

    @Override
    public HttpRequestCompletion<Long> download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
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

    // ********************************************************************
    // request building helpers
    // ********************************************************************

    private SimpleHttpRequest getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.get(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return builder.build();
    }

    private SimpleHttpRequest entityRequest(Function<String, SimpleRequestBuilder> factory, String url,
        Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        if (requestBody == null) {
            SimpleRequestBuilder builder = factory.apply(url);
            HttpClientUtils.setFormBody(builder, params == null ? new HashMap<>() : params);
            applyHeaders(builder, headers);
            return builder.build();
        }
        SimpleRequestBuilder builder = factory.apply(HttpClientUtils.appendParam(url, params));
        builder.setBody(serialize(requestBody, listeners), getContentType(requestBody, safe(headers)));
        applyHeaders(builder, createHeaders(headers, requestBody));
        return builder.build();
    }

    private SimpleHttpRequest deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.delete(HttpClientUtils.appendParam(url, params));
        if (requestBody != null) {
            builder.setBody(serialize(requestBody, listeners), getContentType(requestBody, safe(headers)));
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

    // ********************************************************************
    // execution helpers
    // ********************************************************************

    private HttpRequestCompletion<String> completion(final SimpleHttpRequest request) {
        HttpRequestCompletionImpl<String> completion = new HttpRequestCompletionImpl<>();
        long start = System.currentTimeMillis();
        asyncClient().execute(request, new FutureCallback<SimpleHttpResponse>() {

            @Override
            public void completed(SimpleHttpResponse response) {
                long networkTime = System.currentTimeMillis() - start;
                if (isSuccess(response.getCode())) {
                    byte[] body = response.getBodyBytes();
                    completion.setResponse(new String(body == null ? new byte[0] : body, charset()));
                } else {
                    completion.setHttpErrorResponse(
                        new HttpErrorResponse(errorMessage(request, response), response(response, networkTime)));
                }
            }

            @Override
            public void failed(Exception ex) {
                completion.setHttpErrorResponse(new HttpErrorResponse(ex.getMessage()));
                completion.completeExceptionally(new HttpException(ex));
            }

            @Override
            public void cancelled() {
                completion.setHttpErrorResponse(new HttpErrorResponse("cancelled"));
            }
        });
        return completion;
    }

    private <R> HttpRequestCompletion<R> completion(final SimpleHttpRequest request, final Class<R> responseType,
        Collection<HttpListener> listeners) {
        HttpRequestCompletionImpl<R> completion = new HttpRequestCompletionImpl<>();
        long start = System.currentTimeMillis();
        asyncClient().execute(request, new FutureCallback<SimpleHttpResponse>() {

            @Override
            public void completed(SimpleHttpResponse response) {
                long networkTime = System.currentTimeMillis() - start;
                if (isSuccess(response.getCode())) {
                    try {
                        ContentType contentType = response.getContentType();
                        HttpEntity entity = new ByteArrayEntity(response.getBodyBytes(),
                            contentType == null ? ContentType.DEFAULT_BINARY : contentType);
                        completion.setResponse(deserialize(entity, responseType, listeners));
                    } catch (IOException e) {
                        completion.completeExceptionally(new HttpException(e));
                    }
                } else {
                    completion.setHttpErrorResponse(
                        new HttpErrorResponse(errorMessage(request, response), response(response, networkTime)));
                }
            }

            @Override
            public void failed(Exception ex) {
                completion.setHttpErrorResponse(new HttpErrorResponse(ex.getMessage()));
                completion.completeExceptionally(new HttpException(ex));
            }

            @Override
            public void cancelled() {
                completion.setHttpErrorResponse(new HttpErrorResponse("cancelled"));
            }
        });
        return completion;
    }
}
