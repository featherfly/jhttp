
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
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import cn.featherfly.jhttp.request.HttpReQuestBuilderImpl;
import cn.featherfly.jhttp.request.HttpRequest;
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
    default S stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        HttpRequest request = requestBuilder.apply(new HttpReQuestBuilderImpl()).getHttpRequest();
        switch (request.getMethod()) {
            case GET:
                return stream(HttpMethod.GET, request.getUrl(), request.getParameters(), request.getHeaders());
            case POST:
                return stream(HttpMethod.POST, request.getUrl(), request.getParameters(), request.getHeaders());
            case PUT:
                return stream(HttpMethod.PUT, request.getUrl(), request.getParameters(), request.getHeaders());
            case DELETE:
                return stream(HttpMethod.DELETE, request.getUrl(), request.getParameters(), request.getHeaders());
            case PATCH:
                return stream(HttpMethod.PATCH, request.getUrl(), request.getParameters(), request.getHeaders());
            default:
                throw new HttpException("unsupport http method " + request.getMethod().toString());
        }
    }

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
     * @param httpMethod the http method
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
     * @param httpMethod the http method
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
    S stream(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers);

    /**
     * Stream.
     *
     * @param httpMethod the http method
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
     * @param httpMethod the http method
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
    S stream(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers);
}
