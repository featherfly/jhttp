
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
import java.util.Map;
import java.util.function.Function;

import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

/**
 * Stream Client.
 *
 * @author zhongj
 * @param <S> the generic type
 */
public interface HttpStreamClient<S> {

    /**
     * Stream.
     *
     * @param requestBuilder the request builder
     * @return the input stream
     */
    S stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder);

    /**
     * Stream.
     *
     * @param url the url
     * @return the input stream
     */
    default S stream(String url) {
        return stream(HttpMethod.GET, url);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url) {
        return stream(httpMethod, url, Collections.emptyMap());
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param params the params
     * @return the input stream
     */
    default S stream(String url, Map<String, Serializable> params) {
        return stream(HttpMethod.GET, url, params);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Map<String, Serializable> params) {
        return stream(httpMethod, url, params, Collections.emptyMap());
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the input stream
     */
    default S stream(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return stream(HttpMethod.GET, url, params, headers);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers) {
        return stream(httpMethod, url, params, headers, Collections.emptySet());
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return stream(httpMethod, url, null, params, headers, listeners);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the input stream
     */
    default S stream(String url, Object requestBody) {
        return stream(HttpMethod.GET, url, requestBody);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Object requestBody) {
        return stream(httpMethod, url, requestBody, Collections.emptyMap());
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the input stream
     */
    default S stream(String url, Object requestBody, Map<String, String> headers) {
        return stream(HttpMethod.GET, url, requestBody, headers);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers) {
        return stream(httpMethod, url, requestBody, headers, Collections.emptySet());
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param listeners the listeners
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers,
        Collection<HttpListener> listeners) {
        return stream(httpMethod, url, requestBody, Collections.emptyMap(), headers, listeners);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return the input stream
     */
    default S stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return stream(httpMethod, url, requestBody, params, headers, Collections.emptySet());
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param listeners the listeners
     * @return the input stream
     */
    S stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Collection<HttpListener> listeners);
}
