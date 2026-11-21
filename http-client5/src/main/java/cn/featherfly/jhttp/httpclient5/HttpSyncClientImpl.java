package cn.featherfly.jhttp.httpclient5;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpSyncClient;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.BasicMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl.FormMethodHttpRequestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequest;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

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
     * Instantiates a new http client from the provider clients created by the builder.
     *
     * @param client the classic client
     * @param asyncClient the async client
     * @param headers the default headers
     * @param listeners the global listeners
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpSyncClientImpl(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, Set<HttpListener> listeners, SerializableStrategy serialization,
        ContentType contentType) {
        super(client, asyncClient, headers, listeners, serialization, contentType);
    }

    @Override
    public String request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
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
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
            .getHttpRequest();
        return get0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> R get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.GET, listeners))
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
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
            .getHttpRequest();
        return head0(request.getUrl(), request.getParameters(), request.getHeaders(), request.getListeners());
    }

    @Override
    public <R> R head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.HEAD, listeners))
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
        HttpHead head = new HttpHead(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(head, createHeaders(headers));
        return request(head);
    }

    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers, Class<R> responseType,
        Collection<HttpListener> listeners) {
        return head0(url, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R head0(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        HttpHead head = new HttpHead(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(head, createHeaders(headers));
        return request(head, responseType, finalListeners);
    }

    @Override
    public String post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.POST, listeners))
            .getHttpRequest();
        return post0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            responseType, request.getListeners());
    }

    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, getFinalListeners(listeners));
    }

    private String post0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> finalListeners) {
        return request(entityRequest(HttpPost::new, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return post0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R post0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest(HttpPost::new, url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
            .getHttpRequest();
        return put0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PUT, listeners))
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
        return request(entityRequest(HttpPut::new, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return put0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R put0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest(HttpPut::new, url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
            .getHttpRequest();
        return patch0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder, Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new FormMethodHttpRequestBuilderImpl(HttpMethod.PATCH, listeners))
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
        return request(entityRequest(HttpPatch::new, url, requestBody, params, headers, finalListeners));
    }

    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> listeners) {
        return patch0(url, requestBody, params, headers, responseType, getFinalListeners(listeners));
    }

    private <R> R patch0(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> finalListeners) {
        return request(entityRequest(HttpPatch::new, url, requestBody, params, headers, finalListeners), responseType,
            finalListeners);
    }

    @Override
    public String delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
            .getHttpRequest();
        return delete0(request.getUrl(), request.getRequestBody(), request.getParameters(), request.getHeaders(),
            request.getListeners());
    }

    @Override
    public <R> R delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        HttpRequest request = requestBuilder.apply(new BasicMethodHttpRequestBuilderImpl(HttpMethod.DELETE, listeners))
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
                case POST:
                    HttpPost post = new HttpPost(url);
                    HttpClientUtils.applyHeaders(post, createHeaders(headers));
                    post.setEntity(HttpClientUtils.createRequestBody(params));
                    return stream(post);
                case PUT:
                    HttpPut put = new HttpPut(url);
                    HttpClientUtils.applyHeaders(put, createHeaders(headers));
                    put.setEntity(HttpClientUtils.createRequestBody(params));
                    return stream(put);
                case DELETE:
                    HttpDelete delete = new HttpDelete(HttpClientUtils.appendParam(url, params));
                    HttpClientUtils.applyHeaders(delete, createHeaders(headers));
                    return stream(delete);
                case HEAD:
                    HttpHead head = new HttpHead(HttpClientUtils.appendParam(url, params));
                    HttpClientUtils.applyHeaders(head, createHeaders(headers));
                    return stream(head);
                case PATCH:
                    HttpPatch patch = new HttpPatch(url);
                    HttpClientUtils.applyHeaders(patch, createHeaders(headers));
                    patch.setEntity(HttpClientUtils.createRequestBody(params));
                    return stream(patch);
                default:
                    throw new HttpException("unsupport http method " + httpMethod.toString());
            }
        } else {
            switch (httpMethod) {
                case GET:
                    throw new HttpException("http get method can not send request body");
                case POST:
                    return stream(entityRequest(HttpPost::new, url, requestBody, params, headers, finalListeners));
                case PUT:
                    return stream(entityRequest(HttpPut::new, url, requestBody, params, headers, finalListeners));
                case DELETE:
                    return stream(deleteRequest(url, requestBody, params, headers, finalListeners));
                case HEAD:
                    throw new HttpException("http head method can not send request body");
                case PATCH:
                    return stream(entityRequest(HttpPatch::new, url, requestBody, params, headers, finalListeners));
                default:
                    throw new HttpException("unsupport http method " + httpMethod.toString());
            }
        }
    }

    @Override
    public InputStream stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl(listeners)).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return stream0(HttpMethod.GET, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
            case POST:
                return stream0(HttpMethod.POST, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case PUT:
                return stream0(HttpMethod.PUT, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case DELETE:
                return stream0(HttpMethod.DELETE, request.getUrl(), request.getRequestBody(), request.getParameters(),
                    request.getHeaders(), request.getListeners());
            case HEAD:
                return stream0(HttpMethod.HEAD, request.getUrl(), null, request.getParameters(), request.getHeaders(),
                    request.getListeners());
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

    private HttpUriRequestBase getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        HttpGet request = new HttpGet(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers));
        return request;
    }

    private HttpUriRequestBase entityRequest(Function<String, HttpUriRequestBase> factory, String url,
        Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        if (requestBody == null) {
            HttpUriRequestBase request = factory.apply(url);
            HttpClientUtils.applyHeaders(request, createHeaders(headers));
            request.setEntity(HttpClientUtils.createRequestBody(params));
            return request;
        }
        HttpUriRequestBase request = factory.apply(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers, requestBody));
        request.setEntity(new ByteArrayEntity(serialize(requestBody, listeners), getContentType(requestBody, safe(headers))));
        return request;
    }

    private HttpUriRequestBase deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners) {
        HttpDelete request = new HttpDelete(HttpClientUtils.appendParam(url, params));
        if (requestBody == null) {
            HttpClientUtils.applyHeaders(request, createHeaders(headers));
        } else {
            HttpClientUtils.applyHeaders(request, createHeaders(headers, requestBody));
            request.setEntity(new ByteArrayEntity(serialize(requestBody, listeners), getContentType(requestBody, safe(headers))));
        }
        return request;
    }

    private Map<String, String> safe(Map<String, String> headers) {
        return headers == null ? new HashMap<>() : headers;
    }

    // ********************************************************************
    // execution helpers
    // ********************************************************************

    private String request(org.apache.hc.core5.http.ClassicHttpRequest request) {
        try (CloseableHttpResponse response = getSuccessResponse(request)) {
            HttpEntity entity = response.getEntity();
            return entity == null ? "" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException | ParseException e) {
            throw new HttpException(e);
        }
    }

    private <R> R request(org.apache.hc.core5.http.ClassicHttpRequest request, Class<R> responseType,
        Collection<HttpListener> listeners) {
        try (CloseableHttpResponse response = getSuccessResponse(request)) {
            HttpEntity entity = response.getEntity();
            return entity == null ? null : deserialize(entity, responseType, listeners);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private InputStream stream(org.apache.hc.core5.http.ClassicHttpRequest request) {
        final CloseableHttpResponse response = getSuccessResponse(request);
        try {
            HttpEntity entity = response.getEntity();
            InputStream is = entity == null ? new ByteArrayInputStream(new byte[0]) : entity.getContent();
            return new FilterInputStream(is) {

                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        response.close();
                    }
                }
            };
        } catch (IOException e) {
            try {
                response.close();
            } catch (IOException ignored) {
                // ignore
            }
            throw new HttpException(e);
        }
    }
}
