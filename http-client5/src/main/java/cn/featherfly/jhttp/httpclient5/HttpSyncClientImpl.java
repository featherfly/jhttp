package cn.featherfly.jhttp.httpclient5;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
import cn.featherfly.jhttp.HttpMethod;
import cn.featherfly.jhttp.HttpSyncClient;

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
     * @param serialization the serialization
     * @param contentType the request content type
     */
    protected HttpSyncClientImpl(CloseableHttpClient client, CloseableHttpAsyncClient asyncClient,
        Map<String, String> headers, SerializableStrategy serialization, ContentType contentType) {
        super(client, asyncClient, headers, serialization, contentType);
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
        HttpHead request = new HttpHead(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers));
        return request(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        HttpHead request = new HttpHead(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers));
        return request(request, responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest(HttpPost::new, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest(HttpPost::new, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest(HttpPut::new, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest(HttpPut::new, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return request(entityRequest(HttpPatch::new, url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(entityRequest(HttpPatch::new, url, requestBody, params, headers), responseType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return request(deleteRequest(url, requestBody, params, headers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return request(deleteRequest(url, requestBody, params, headers), responseType);
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
                return stream(entityRequest(HttpPost::new, url, requestBody, new HashMap<>(), headers));
            case PUT:
                return stream(entityRequest(HttpPut::new, url, requestBody, new HashMap<>(), headers));
            case DELETE:
                return stream(entityRequest(HttpDelete::new, url, requestBody, new HashMap<>(), headers));
            case HEAD:
                throw new HttpException("http head method can not send request body");
            case PATCH:
                return stream(entityRequest(HttpPatch::new, url, requestBody, new HashMap<>(), headers));
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }

    private HttpUriRequestBase getRequest(String url, Map<String, Serializable> params, Map<String, String> headers) {
        HttpGet request = new HttpGet(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers));
        return request;
    }

    private HttpUriRequestBase entityRequest(Function<String, HttpUriRequestBase> factory, String url,
        Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        if (requestBody == null) {
            HttpUriRequestBase request = factory.apply(url);
            HttpClientUtils.applyHeaders(request, createHeaders(headers));
            request.setEntity(HttpClientUtils.createRequestBody(params));
            return request;
        }
        HttpUriRequestBase request = factory.apply(HttpClientUtils.appendParam(url, params));
        HttpClientUtils.applyHeaders(request, createHeaders(headers, requestBody));
        request.setEntity(new ByteArrayEntity(serialize(requestBody), getContentType(requestBody, safe(headers))));
        return request;
    }

    private HttpUriRequestBase deleteRequest(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        HttpDelete request = new HttpDelete(HttpClientUtils.appendParam(url, params));
        if (requestBody == null) {
            HttpClientUtils.applyHeaders(request, createHeaders(headers));
        } else {
            HttpClientUtils.applyHeaders(request, createHeaders(headers, requestBody));
            request.setEntity(new ByteArrayEntity(serialize(requestBody), getContentType(requestBody, safe(headers))));
        }
        return request;
    }

    private Map<String, String> safe(Map<String, String> headers) {
        return headers == null ? new HashMap<>() : headers;
    }

    private String request(org.apache.hc.core5.http.ClassicHttpRequest request) {
        try (CloseableHttpResponse response = getSuccessResponse(request)) {
            HttpEntity entity = response.getEntity();
            return entity == null ? "" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException | ParseException e) {
            throw new HttpException(e);
        }
    }

    private <R> R request(org.apache.hc.core5.http.ClassicHttpRequest request, Class<R> responseType) {
        try (CloseableHttpResponse response = getSuccessResponse(request)) {
            HttpEntity entity = response.getEntity();
            return entity == null ? null : deserialize(entity, responseType);
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
