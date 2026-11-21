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
import cn.featherfly.jhttp.HttpAsyncClient;
import cn.featherfly.jhttp.HttpErrorResponse;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRequestCompletion;
import cn.featherfly.jhttp.HttpRequestCompletionImpl;

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
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpAsyncClientImpl(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, SerializableStrategy serialization, ContentType contentType) {
        super(client, asyncClient, headers, serialization, contentType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> get(String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(getRequest(url, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return completion(getRequest(url, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> head(String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return completion(builder.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        SimpleRequestBuilder builder = SimpleRequestBuilder.head(HttpClientUtils.appendParam(url, params));
        applyHeaders(builder, headers);
        return completion(builder.build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest(SimpleRequestBuilder::post, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest(SimpleRequestBuilder::put, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest(SimpleRequestBuilder::patch, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(deleteRequest(url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(deleteRequest(url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
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

    private <R> HttpRequestCompletion<R> completion(final SimpleHttpRequest request, final Class<R> responseType) {
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
                        completion.setResponse(deserialize(entity, responseType));
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
