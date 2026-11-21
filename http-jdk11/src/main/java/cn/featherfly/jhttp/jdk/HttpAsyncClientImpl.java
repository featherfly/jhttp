package cn.featherfly.jhttp.jdk;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cn.featherfly.common.serialization.SerializableStrategy;
import cn.featherfly.jhttp.HttpAsyncClient;
import cn.featherfly.jhttp.HttpErrorResponse;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpRequestCompletion;
import cn.featherfly.jhttp.HttpRequestCompletionImpl;

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
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpAsyncClientImpl(java.net.http.HttpClient client, Map<String, String> headers,
        SerializableStrategy serialization, String contentType) {
        super(client, headers, serialization, contentType);
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
        return completion(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return completion(requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers))
            .method("HEAD", BodyPublishers.noBody()).build(), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest("POST", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest("POST", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest("PUT", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest("PUT", url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestCompletion<String> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return completion(entityRequest("PATCH", url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return completion(entityRequest("PATCH", url, requestBody, params, headers), responseType);
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
        return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers, requestBody))
            .method(method, BodyPublishers.ofByteArray(serialize(requestBody))).build();
    }

    private HttpRequest deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        if (requestBody == null) {
            return requestBuilder(JdkHttpUtils.appendParam(url, params), createHeaders(headers)).DELETE().build();
        }
        return entityRequest("DELETE", url, requestBody, params, headers);
    }

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

    private <R> HttpRequestCompletion<R> completion(final HttpRequest request, final Class<R> responseType) {
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
                        response.headers().firstValue("content-type").orElse(null), responseType));
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
