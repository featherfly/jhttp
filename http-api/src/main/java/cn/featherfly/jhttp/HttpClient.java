
/*
 * All rights Reserved, Designed By zhongj
 * @Title: Client.java
 * @Package cn.featherfly.common.http
 * @Description: Client
 * @author: zhongj
 * @date: 2022-11-07 17:25:07
 * @Copyright: 2022 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

/**
 * Client.
 *
 * @author zhongj
 * @param <RS> the generic request response type
 * @param <C> the generic type
 */
public interface HttpClient<RS, C> {

    /**
     * Shutdown.
     */
    void shutdown();

    /**
     * Whether the deserializer is chosen by the response content-type. Implementations
     * override this with their real configuration.
     *
     * @return true if enabled
     */
    default boolean isDeserializeWithContentType() {
        return false;
    }

    /**
     * get request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * get request.
     *
     * @param url the url
     * @return response content
     */
    default RS get(String url) {
        return get(url, new HashMap<>(0));
    }

    /**
     * get request with params.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS get(String url, Map<String, Serializable> params) {
        return get(url, params, new HashMap<>(0));
    }

    /**
     * get request with params.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS get(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return get(url, params, headers, Collections.emptySet());
    }

    /**
     * get request with params.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * post request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * post request.
     *
     * @param url the url
     * @return response content
     */
    default RS post(String url) {
        return post(url, new HashMap<>(0));
    }

    /**
     * Post params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS post(String url, Map<String, Serializable> params) {
        return post(url, params, new HashMap<>(0));
    }

    /**
     * Post params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS post(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return post(url, null, params, headers);
    }

    /**
     * Post requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @return response content
     */
    default RS post(String url, Object requestBody) {
        return post(url, requestBody, new HashMap<>(0));
    }

    /**
     * Post requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return response content
     */
    default RS post(String url, Object requestBody, Map<String, String> headers) {
        return post(url, requestBody, new HashMap<>(0), headers);
    }

    /**
     * Post requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return post(url, requestBody, params, headers, Collections.emptySet());
    }

    /**
     * Post requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS post(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * put request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * Put.
     *
     * @param url the url
     * @return response content
     */
    default RS put(String url) {
        return put(url, new HashMap<>(0));
    }

    /**
     * Put params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS put(String url, Map<String, Serializable> params) {
        return put(url, params, new HashMap<>(0));
    }

    /**
     * Put params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS put(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return put(url, null, params, headers);
    }

    /**
     * Put requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @return response content
     */
    default RS put(String url, Object requestBody) {
        return put(url, requestBody, new HashMap<>(0));
    }

    /**
     * Put requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return response content
     */
    default RS put(String url, Object requestBody, Map<String, String> headers) {
        return put(url, requestBody, new HashMap<>(0), headers);
    }

    /**
     * Put requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return put(url, requestBody, params, headers, Collections.emptySet());
    }

    /**
     * Put requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS put(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * delete request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * Delete.
     *
     * @param url the url
     * @return response content
     */
    default RS delete(String url) {
        return delete(url, new HashMap<>(0));
    }

    /**
     * get request with params.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS delete(String url, Map<String, Serializable> params) {
        return delete(url, params, new HashMap<>(0));
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS delete(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return delete(url, null, params, headers);
    }

    /**
     * Delete requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @return response content
     */
    default RS delete(String url, Object requestBody) {
        return delete(url, requestBody, new HashMap<>(0));
    }

    /**
     * Delete requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return response content
     */
    default RS delete(String url, Object requestBody, Map<String, String> headers) {
        return delete(url, requestBody, new HashMap<>(0), headers);
    }

    /**
     * Delete requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return delete(url, requestBody, params, headers, Collections.emptySet());
    }

    /**
     * Delete requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS delete(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * patch request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * patch request.
     *
     * @param url the url
     * @return response content
     */
    default RS patch(String url) {
        return patch(url, new HashMap<>(0));
    }

    /**
     * patch request params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS patch(String url, Map<String, Serializable> params) {
        return patch(url, params, new HashMap<>(0));
    }

    /**
     * patch request params with FormBody.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS patch(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return patch(url, null, new HashMap<>(0), headers);
    }

    /**
     * patch request requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @return response content
     */
    default RS patch(String url, Object requestBody) {
        return patch(url, requestBody, new HashMap<>(0));
    }

    /**
     * patch request requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return response content
     */
    default RS patch(String url, Object requestBody, Map<String, String> headers) {
        return patch(url, requestBody, new HashMap<>(0), headers);
    }

    /**
     * patch request requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers) {
        return patch(url, requestBody, params, headers, Collections.emptySet());
    }

    /**
     * patch request requestBody with medieType format.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS patch(String url, Object requestBody, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * head request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * head request.
     *
     * @param url the url
     * @return response content
     */
    default RS head(String url) {
        return head(url, new HashMap<>(0));
    }

    /**
     * head request with params.
     *
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS head(String url, Map<String, Serializable> params) {
        return head(url, params, new HashMap<>(0));
    }

    /**
     * head request with params.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return head(url, params, headers, Collections.emptySet());
    }

    /**
     * head request with params.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return response content
     */
    RS head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners);

    /**
     * Request.
     *
     * @param requestBuilder the request builder
     * @return the rs
     */
    RS request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @return response content
     */
    default RS request(HttpMethod httpMethod, String url) {
        return request(httpMethod, url, new HashMap<>(0));
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @return response content
     */
    default RS request(HttpMethod httpMethod, String url, Map<String, Serializable> params) {
        return request(httpMethod, url, params, new HashMap<>(0));
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return response content
     */
    default RS request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                return get(url, params, headers);
            case POST:
                return post(url, params, headers);
            case PUT:
                return put(url, params, headers);
            case DELETE:
                return delete(url, params, headers);
            case HEAD:
                return head(url, params, headers);
            case PATCH:
                return patch(url, params, headers);
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * request body with medieType format.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @return response content
     */
    default RS request(HttpMethod httpMethod, String url, Object requestBody) {
        return request(httpMethod, url, requestBody, new HashMap<>(0));
    }

    /**
     * Post requestBody with medieType format.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return response content
     */
    default RS request(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers) {
        switch (httpMethod) {
            case GET:
                throw new HttpException("http get method can not send request body");
            //                return get(url, new HashMap<>(0), headers);
            case POST:
                return post(url, requestBody, headers);
            case PUT:
                return put(url, requestBody, headers);
            case DELETE:
                return delete(url, requestBody, headers);
            case HEAD:
                throw new HttpException("http head method can not send request body");
            //                return head(url, new HashMap<>(0), headers);
            case PATCH:
                return patch(url, requestBody, headers);
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }
}
