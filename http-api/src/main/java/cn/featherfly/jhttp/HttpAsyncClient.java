
/*
 * All rights Reserved, Designed By zhongj
 * @Title: HttpAsyncClient.java
 * @Package cn.featherfly.common.http
 * @Description: HttpAsyncClient
 * @author: zhongj
 * @date: 2022-12-02 15:38:02
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
 * HttpAsyncClient.
 *
 * @author zhongj
 */
public interface HttpAsyncClient extends HttpClient<HttpRequestCompletion<String>, HttpAsyncClient>,
    HttpDownloadClient<HttpRequestCompletion<Long>> {

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the rs
     */
    <R> HttpRequestCompletion<R> request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> request(HttpMethod httpMethod, String url, Class<R> responseType) {
        return request(httpMethod, url, new HashMap<>(0), responseType);
    }

    /**
     * request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Class<R> responseType) {
        return request(httpMethod, url, params, new HashMap<>(0), responseType);
    }

    /**
     * request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        switch (httpMethod) {
            case GET:
                return get(url, params, headers, responseType);
            case POST:
                return post(url, params, headers, responseType);
            case PUT:
                return put(url, params, headers, responseType);
            case DELETE:
                return delete(url, headers, responseType);
            case HEAD:
                return head(url, params, headers, responseType);
            case PATCH:
                return patch(url, params, headers, responseType);
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * Post requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> request(HttpMethod httpMethod, String url, Object requestBody,
        Class<R> responseType) {
        return request(httpMethod, url, requestBody, new HashMap<>(0), responseType);
    }

    /**
     * Post requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> request(HttpMethod httpMethod, String url, Object requestBody,
        Map<String, String> headers, Class<R> responseType) {
        switch (httpMethod) {
            case GET:
                throw new HttpException("http get method can not send request body");
            //                return get(url, new HashMap<>(0), headers);
            case POST:
                return post(url, requestBody, headers, responseType);
            case PUT:
                return put(url, requestBody, headers, responseType);
            case DELETE:
                return delete(url, requestBody, headers, responseType);
            case HEAD:
                throw new HttpException("http head method can not send request body");
            //                return head(url, new HashMap<>(0), headers);
            case PATCH:
                return patch(url, requestBody, headers, responseType);
            default:
                throw new HttpException("unsupport http method " + httpMethod.toString());
        }
    }

    /**
     * get request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * get request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> get(String url, Class<R> responseType) {
        return get(url, new HashMap<>(0), responseType);
    }

    /**
     * get request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Class<R> responseType) {
        return get(url, params, new HashMap<>(0), responseType);
    }

    /**
     * get request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return get(url, params, headers, responseType, Collections.emptySet());
    }

    /**
     * get request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return the http request completion
     */
    <R> HttpRequestCompletion<R> get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> httpListeners);

    /**
     * head request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * head request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> head(String url, Class<R> responseType) {
        return head(url, new HashMap<>(0), responseType);
    }

    /**
     * head request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Class<R> responseType) {
        return head(url, params, new HashMap<>(0), responseType);
    }

    /**
     * head request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return head(url, params, headers, responseType, Collections.emptySet());
    }

    /**
     * head request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return the http request completion
     */
    <R> HttpRequestCompletion<R> head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType, Collection<HttpListener> httpListeners);

    /**
     * post request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * Post params with FormBody and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> post(String url, Map<String, Serializable> params, Class<R> responseType) {
        return post(url, params, new HashMap<>(0), responseType);
    }

    /**
     * Post params with FormBody and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> post(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return post(url, null, params, headers, responseType);
    }

    /**
     * Post requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> post(String url, Object requestBody, Class<R> responseType) {
        return post(url, requestBody, new HashMap<>(0), responseType);
    }

    /**
     * Post requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, String> headers,
        Class<R> responseType) {
        return post(url, requestBody, new HashMap<>(0), headers, responseType);
    }

    /**
     * Post params with FormBody and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return post(url, requestBody, params, headers, responseType, Collections.emptySet());
    }

    /**
     * Post params with FormBody and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return response string
     */
    <R> HttpRequestCompletion<R> post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> httpListeners);

    /**
     * put request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * Put params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> put(String url, Map<String, Serializable> params, Class<R> responseType) {
        return put(url, params, new HashMap<>(0), responseType);
    }

    /**
     * Put params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> put(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return put(url, null, params, headers, responseType);
    }

    /**
     * Put requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the responset type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> put(String url, Object requestBody, Class<R> responseType) {
        return put(url, requestBody, new HashMap<>(0), responseType);
    }

    /**
     * Put requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the responset type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, String> headers,
        Class<R> responseType) {
        return put(url, requestBody, new HashMap<>(0), headers, responseType);
    }

    /**
     * Put params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return put(url, requestBody, params, headers, responseType, Collections.emptySet());
    }

    /**
     * Put params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return the http request completion
     */
    <R> HttpRequestCompletion<R> put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> httpListeners);

    /**
     * patch request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * patch request params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> patch(String url, Map<String, Serializable> params, Class<R> responseType) {
        return patch(url, params, new HashMap<>(0), responseType);
    }

    /**
     * patch request params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> patch(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return patch(url, null, params, headers, responseType);
    }

    /**
     * patch request params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return patch(url, requestBody, params, headers, responseType, Collections.emptySet());
    }

    /**
     * patch request requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the responset type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Class<R> responseType) {
        return patch(url, requestBody, new HashMap<>(0), responseType);
    }

    /**
     * patch request requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the responset type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, String> headers,
        Class<R> responseType) {
        return patch(url, requestBody, new HashMap<>(0), headers, responseType);
    }

    /**
     * patch request params with FormBody.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return the http request completion
     */
    <R> HttpRequestCompletion<R> patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> httpListeners);

    /**
     * delete request with params and deserialize response .
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return responseType instance
     */
    <R> HttpRequestCompletion<R> delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType);

    /**
     * Delete request and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> delete(String url, Class<R> responseType) {
        return delete(url, new HashMap<>(0), responseType);
    }

    /**
     * Delete request and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> delete(String url, Map<String, Serializable> params, Class<R> responseType) {
        return delete(url, params, new HashMap<>(0), responseType);
    }

    /**
     * Delete request and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return response string
     */
    default <R> HttpRequestCompletion<R> delete(String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return delete(url, null, params, headers, responseType);
    }

    /**
     * Delete requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Class<R> responseType) {
        return delete(url, requestBody, new HashMap<>(0), responseType);
    }

    /**
     * Delete requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, String> headers,
        Class<R> responseType) {
        return delete(url, requestBody, new HashMap<>(0), headers, responseType);
    }

    /**
     * Delete requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the http request completion
     */
    default <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return delete(url, requestBody, params, headers, responseType, Collections.emptySet());
    }

    /**
     * Delete requestBody with medieType format and deserialize response.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @param httpListeners the http listeners
     * @return the http request completion
     */
    <R> HttpRequestCompletion<R> delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType, Collection<HttpListener> httpListeners);

}
