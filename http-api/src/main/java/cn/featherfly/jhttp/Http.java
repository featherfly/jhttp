
package cn.featherfly.jhttp;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import cn.featherfly.jhttp.HttpDownloadClient.ProgressConsumer;
import cn.featherfly.jhttp.request.BasicMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.FormMethodHttpRequestBuilder;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;

/**
 * Http.
 *
 * @author zhongj
 */
public final class Http {

    private static final HttpSyncClient CLIENT = HttpClientBuilder.sync().build();

    private Http() {
    }

    /**
     * On.
     *
     * @param listener the listener
     * @return the http sync client
     * @see cn.featherfly.jhttp.HttpClient#on(cn.featherfly.jhttp.HttpListener)
     */
    public static HttpSyncClient on(HttpListener listener) {
        return CLIENT.on(listener);
    }

    /**
     * Un.
     *
     * @param listener the listener
     * @return the http sync client
     * @see cn.featherfly.jhttp.HttpClient#un(cn.featherfly.jhttp.HttpListener)
     */
    public static HttpSyncClient un(HttpListener listener) {
        return CLIENT.un(listener);
    }

    /**
     * Shutdown.
     */
    public static void shutdown() {
        CLIENT.shutdown();
    }

    /**
     * Checks if is deserialize with content type.
     *
     * @return true, if is deserialize with content type
     * @see cn.featherfly.common.http.HttpSyncClientImpl#isDeserializeWithContentType()
     */
    public static boolean isDeserializeWithContentType() {
        return CLIENT.isDeserializeWithContentType();
    }

    /**
     * Gets the.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#get(java.util.function.Function)
     */
    public static String get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.get(requestBuilder);
    }

    /**
     * Gets the.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#get(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R get(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.get(requestBuilder, responseType);
    }

    /**
     * Gets the.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String)
     */
    public static String get(String url) {
        return CLIENT.get(url);
    }

    /**
     * Gets the.
     *
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String,
     *      java.util.Map)
     */
    public static String get(String url, Map<String, Serializable> params) {
        return CLIENT.get(url, params);
    }

    /**
     * Gets the.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public static String get(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.get(url, params, headers);
    }

    /**
     * Gets the.
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String,
     *      java.lang.Class)
     */
    public static <R> R get(String url, Class<R> responseType) {
        return CLIENT.get(url, responseType);
    }

    /**
     * Gets the.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R get(String url, Map<String, Serializable> params, Class<R> responseType) {
        return CLIENT.get(url, params, responseType);
    }

    /**
     * Gets the.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#get(java.lang.String,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R get(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.get(url, params, headers, responseType);
    }

    /**
     * Head.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#head(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.head(requestBuilder, responseType);
    }

    /**
     * Head.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#head(java.util.function.Function)
     */
    public static String head(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.head(requestBuilder);
    }

    /**
     * Head.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String)
     */
    public static String head(String url) {
        return CLIENT.head(url);
    }

    /**
     * Head.
     *
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String,
     *      java.util.Map)
     */
    public static String head(String url, Map<String, Serializable> params) {
        return CLIENT.head(url, params);
    }

    /**
     * Head.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public static String head(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.head(url, params, headers);
    }

    /**
     * Head.
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String,
     *      java.lang.Class)
     */
    public static <R> R head(String url, Class<R> responseType) {
        return CLIENT.head(url, responseType);
    }

    /**
     * Head.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R head(String url, Map<String, Serializable> params, Class<R> responseType) {
        return CLIENT.head(url, params, responseType);
    }

    /**
     * Head.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#head(java.lang.String,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R head(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.head(url, params, headers, responseType);
    }

    /**
     * Post.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#post(java.util.function.Function)
     */
    public static String post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.post(requestBuilder);
    }

    /**
     * Post.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return the string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map)
     */
    public static String post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.post(url, requestBody, params, headers);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R post(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return CLIENT.post(url, requestBody, params, headers, responseType);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#post(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R post(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.post(requestBuilder, responseType);
    }

    /**
     * Post.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String)
     */
    public static String post(String url) {
        return CLIENT.post(url);
    }

    /**
     * Post.
     *
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.util.Map)
     */
    public static String post(String url, Map<String, Serializable> params) {
        return CLIENT.post(url, params);
    }

    /**
     * Post.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public static String post(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.post(url, params, headers);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R post(String url, Map<String, Serializable> params, Class<R> responseType) {
        return CLIENT.post(url, params, responseType);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R post(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.post(url, params, headers, responseType);
    }

    /**
     * Post.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.lang.Object)
     */
    public static String post(String url, Object requestBody) {
        return CLIENT.post(url, requestBody);
    }

    /**
     * Post.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.lang.Object, java.util.Map)
     */
    public static String post(String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.post(url, requestBody, headers);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public static <R> R post(String url, Object requestBody, Class<R> responseType) {
        return CLIENT.post(url, requestBody, responseType);
    }

    /**
     * Post.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#post(java.lang.String,
     *      java.lang.Object, java.util.Map, java.lang.Class)
     */
    public static <R> R post(String url, Object requestBody, Map<String, String> headers, Class<R> responseType) {
        return CLIENT.post(url, requestBody, headers, responseType);
    }

    /**
     * Put.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#put(java.util.function.Function)
     */
    public static String put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.put(requestBuilder);
    }

    /**
     * Put.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return the string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map)
     */
    public static String put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.put(url, requestBody, params, headers);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R put(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return CLIENT.put(url, requestBody, params, headers, responseType);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#put(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R put(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.put(requestBuilder, responseType);
    }

    /**
     * Put.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String)
     */
    public static String put(String url) {
        return CLIENT.put(url);
    }

    /**
     * Put.
     *
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.util.Map)
     */
    public static String put(String url, Map<String, Serializable> params) {
        return CLIENT.put(url, params);
    }

    /**
     * Put.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public static String put(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.put(url, params, headers);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R put(String url, Map<String, Serializable> params, Class<R> responseType) {
        return CLIENT.put(url, params, responseType);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R put(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.put(url, params, headers, responseType);
    }

    /**
     * Put.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.lang.Object)
     */
    public static String put(String url, Object requestBody) {
        return CLIENT.put(url, requestBody);
    }

    /**
     * Put.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.lang.Object, java.util.Map)
     */
    public static String put(String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.put(url, requestBody, headers);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public static <R> R put(String url, Object requestBody, Class<R> responseType) {
        return CLIENT.put(url, requestBody, responseType);
    }

    /**
     * Put.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#put(java.lang.String,
     *      java.lang.Object, java.util.Map, java.lang.Class)
     */
    public static <R> R put(String url, Object requestBody, Map<String, String> headers, Class<R> responseType) {
        return CLIENT.put(url, requestBody, headers, responseType);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return the string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map)
     */
    public static String patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.patch(url, requestBody, params, headers);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R patch(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return CLIENT.patch(url, requestBody, params, headers, responseType);
    }

    /**
     * Patch.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#patch(java.util.function.Function)
     */
    public static String patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.patch(requestBuilder);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#patch(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R patch(Function<FormMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.patch(requestBuilder, responseType);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String)
     */
    public static String patch(String url) {
        return CLIENT.patch(url);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.util.Map)
     */
    public static String patch(String url, Map<String, Serializable> params) {
        return CLIENT.patch(url, params);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.util.Map, java.util.Map)
     */
    public static String patch(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.patch(url, params, headers);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R patch(String url, Map<String, Serializable> params, Class<R> responseType) {
        return CLIENT.patch(url, params, responseType);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R patch(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.patch(url, params, headers, responseType);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.lang.Object)
     */
    public static String patch(String url, Object requestBody) {
        return CLIENT.patch(url, requestBody);
    }

    /**
     * Patch.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.lang.Object, java.util.Map)
     */
    public static String patch(String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.patch(url, requestBody, headers);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public static <R> R patch(String url, Object requestBody, Class<R> responseType) {
        return CLIENT.patch(url, requestBody, responseType);
    }

    /**
     * Patch.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#patch(java.lang.String,
     *      java.lang.Object, java.util.Map, java.lang.Class)
     */
    public static <R> R patch(String url, Object requestBody, Map<String, String> headers, Class<R> responseType) {
        return CLIENT.patch(url, requestBody, headers, responseType);
    }

    /**
     * Delete.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#delete(java.util.function.Function)
     */
    public static String delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.delete(requestBuilder);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the string
     * @see cn.featherfly.common.http.HttpClient#delete(java.lang.String, java.util.Map,
     *      java.util.Map)
     */
    public static String delete(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.delete(url, params, headers);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return CLIENT.delete(url, requestBody, params, headers, responseType);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param requestBody the request body
     * @param params the params
     * @param headers the headers
     * @return the string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String, java.lang.Object,
     *      java.util.Map, java.util.Map)
     */
    public static String delete(String url, Object requestBody, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.delete(url, requestBody, params, headers);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param requestBuilder the request builder
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#delete(java.util.function.Function,
     *      java.lang.Class)
     */
    public static <R> R delete(Function<BasicMethodHttpRequestBuilder, HttpRequestAware> requestBuilder,
        Class<R> responseType) {
        return CLIENT.delete(requestBuilder, responseType);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClient#delete(java.lang.String, java.util.Map,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R delete(String url, Map<String, Serializable> params, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.delete(url, params, headers, responseType);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String)
     */
    public static String delete(String url) {
        return CLIENT.delete(url);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.util.Map)
     */
    public static String delete(String url, Map<String, String> headers) {
        return CLIENT.delete(url, headers);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.lang.Class)
     */
    public static <R> R delete(String url, Class<R> responseType) {
        return CLIENT.delete(url, responseType);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.util.Map, java.lang.Class)
     */
    public static <R> R delete(String url, Map<String, String> headers, Class<R> responseType) {
        return CLIENT.delete(url, headers, responseType);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.lang.Object)
     */
    public static String delete(String url, Object requestBody) {
        return CLIENT.delete(url, requestBody);
    }

    /**
     * Delete.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.lang.Object, java.util.Map)
     */
    public static String delete(String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.delete(url, requestBody, headers);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.lang.Object, java.lang.Class)
     */
    public static <R> R delete(String url, Object requestBody, Class<R> responseType) {
        return CLIENT.delete(url, requestBody, responseType);
    }

    /**
     * Delete.
     *
     * @param <R> the generic type
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#delete(java.lang.String,
     *      java.lang.Object, java.util.Map, java.lang.Class)
     */
    public static <R> R delete(String url, Object requestBody, Map<String, String> headers, Class<R> responseType) {
        return CLIENT.delete(url, requestBody, headers, responseType);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param output the output
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, OutputStream)
     */
    public static Long download(String url, OutputStream output) {
        return CLIENT.download(url, output);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, OutputStream, BiConsumer)
     */
    public static Long download(String url, OutputStream output, ProgressConsumer progress) {
        return CLIENT.download(url, output, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param localFile the local file
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, File)
     */
    public static Long download(String url, File localFile) {
        return CLIENT.download(url, localFile);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, File, BiConsumer)
     */
    public static Long download(String url, File localFile, ProgressConsumer progress) {
        return CLIENT.download(url, localFile, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param output the output
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(java.lang.String,
     *      java.util.Map, java.io.OutputStream)
     */
    public static Long download(String url, Map<String, Serializable> params, OutputStream output) {
        return CLIENT.download(url, params, output);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, Map, OutputStream,
     *      BiConsumer)
     */
    public static Long download(String url, Map<String, Serializable> params, OutputStream output,
        ProgressConsumer progress) {
        return CLIENT.download(url, params, output, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(java.lang.String,
     *      java.util.Map, java.io.File)
     */
    public static Long download(String url, Map<String, Serializable> params, File localFile) {
        return CLIENT.download(url, params, localFile);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, Map, File, BiConsumer)
     */
    public static Long download(String url, Map<String, Serializable> params, File localFile,
        ProgressConsumer progress) {
        return CLIENT.download(url, params, localFile, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(java.lang.String,
     *      java.util.Map, java.util.Map, java.io.File)
     */
    public static Long download(String url, Map<String, Serializable> params, Map<String, String> headers,
        File localFile) {
        return CLIENT.download(url, params, headers, localFile);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, Map, Map, File,
     *      BiConsumer)
     */
    public static Long download(String url, Map<String, Serializable> params, Map<String, String> headers,
        File localFile, ProgressConsumer progress) {
        return CLIENT.download(url, params, headers, localFile, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(java.lang.String,
     *      java.util.Map, java.util.Map, java.io.OutputStream)
     */
    public static Long download(String url, Map<String, Serializable> params, Map<String, String> headers,
        OutputStream output) {
        return CLIENT.download(url, params, headers, output);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download file size
     * @see cn.featherfly.common.http.HttpSyncClientImpl#download(String, Map, Map, OutputStream,
     *      BiConsumer)
     */
    public static Long download(String url, Map<String, Serializable> params, Map<String, String> headers,
        OutputStream output, ProgressConsumer progress) {
        return CLIENT.download(url, params, headers, output, progress);
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String)
     */
    public static String request(HttpMethod httpMethod, String url) {
        return CLIENT.request(httpMethod, url);
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map)
     */
    public static String request(HttpMethod httpMethod, String url, Map<String, Serializable> params) {
        return CLIENT.request(httpMethod, url, params);
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map)
     */
    public static String request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.request(httpMethod, url, params, headers);
    }

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Class)
     */
    public static <R> R request(HttpMethod httpMethod, String url, Class<R> responseType) {
        return CLIENT.request(httpMethod, url, responseType);
    }

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map, java.lang.Class)
     */
    public static <R> R request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Class<R> responseType) {
        return CLIENT.request(httpMethod, url, params, responseType);
    }

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map, java.lang.Class)
     */
    public static <R> R request(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, Class<R> responseType) {
        return CLIENT.request(httpMethod, url, params, headers, responseType);
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object)
     */
    public static String request(HttpMethod httpMethod, String url, Object requestBody) {
        return CLIENT.request(httpMethod, url, requestBody);
    }

    /**
     * Request.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the response string
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object, java.util.Map)
     */
    public static String request(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.request(httpMethod, url, requestBody, headers);
    }

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object, java.lang.Class)
     */
    public static <R> R request(HttpMethod httpMethod, String url, Object requestBody, Class<R> responseType) {
        return CLIENT.request(httpMethod, url, requestBody, responseType);
    }

    /**
     * Request.
     *
     * @param <R> the generic type
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @param responseType the response type
     * @return the r
     * @see cn.featherfly.common.http.HttpSyncClientImpl#request(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object, java.util.Map, java.lang.Class)
     */
    public static <R> R request(HttpMethod httpMethod, String url, Object requestBody, Map<String, String> headers,
        Class<R> responseType) {
        return CLIENT.request(httpMethod, url, requestBody, headers, responseType);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @return the input stream
     * @see cn.featherfly.common.http.HttpSyncClientImpl#stream(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String)
     */
    public static InputStream stream(HttpMethod httpMethod, String url) {
        return CLIENT.stream(httpMethod, url);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @return the input stream
     * @see cn.featherfly.common.http.HttpSyncClientImpl#stream(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map)
     */
    public static InputStream stream(HttpMethod httpMethod, String url, Map<String, Serializable> params) {
        return CLIENT.stream(httpMethod, url, params);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the input stream
     * @see cn.featherfly.common.http.HttpSyncClientImpl#stream(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map)
     */
    public static InputStream stream(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers) {
        return CLIENT.stream(httpMethod, url, params, headers);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @return the input stream
     * @see cn.featherfly.common.http.HttpSyncClientImpl#stream(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object)
     */
    public static InputStream stream(HttpMethod httpMethod, String url, Object requestBody) {
        return CLIENT.stream(httpMethod, url, requestBody);
    }

    /**
     * Stream.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the input stream
     * @see cn.featherfly.common.http.HttpSyncClientImpl#stream(cn.featherfly.common.http.HttpMethod,
     *      java.lang.String, java.lang.Object, java.util.Map)
     */
    public static InputStream stream(HttpMethod httpMethod, String url, Object requestBody,
        Map<String, String> headers) {
        return CLIENT.stream(httpMethod, url, requestBody, headers);
    }

    /**
     * Stream.
     *
     * @param requestBuilder the request builder
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.util.function.Function)
     */
    public static InputStream stream(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.stream(requestBuilder);
    }

    /**
     * Download.
     *
     * @param requestBuilder the request builder
     * @param output the output
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(java.util.function.Function,
     *      java.io.OutputStream)
     */
    public static Long download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder, OutputStream output) {
        return CLIENT.download(requestBuilder, output);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.lang.String)
     */
    public static InputStream stream(String url) {
        return CLIENT.stream(url);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param params the params
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.lang.String, java.util.Map)
     */
    public static InputStream stream(String url, Map<String, Serializable> params) {
        return CLIENT.stream(url, params);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param output the output
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.io.OutputStream)
     */
    public static Long download(HttpMethod httpMethod, String url, OutputStream output) {
        return CLIENT.download(httpMethod, url, output);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.lang.String, java.util.Map,
     *      java.util.Map)
     */
    public static InputStream stream(String url, Map<String, Serializable> params, Map<String, String> headers) {
        return CLIENT.stream(url, params, headers);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param output the output
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.io.OutputStream, java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, OutputStream output, ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, output, progress);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param requestBody the request body
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.lang.String, java.lang.Object)
     */
    public static InputStream stream(String url, Object requestBody) {
        return CLIENT.stream(url, requestBody);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param localFile the local file
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.io.File)
     */
    public static Long download(HttpMethod httpMethod, String url, File localFile) {
        return CLIENT.download(httpMethod, url, localFile);
    }

    /**
     * Stream.
     *
     * @param url the url
     * @param requestBody the request body
     * @param headers the headers
     * @return the input stream
     * @see cn.featherfly.jhttp.HttpStreamClient#stream(java.lang.String, java.lang.Object,
     *      java.util.Map)
     */
    public static InputStream stream(String url, Object requestBody, Map<String, String> headers) {
        return CLIENT.stream(url, requestBody, headers);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param localFile the local file
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.io.File, java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, File localFile, ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, localFile, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param output the output
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.io.OutputStream)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        OutputStream output) {
        return CLIENT.download(httpMethod, url, params, output);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param output the output
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.io.OutputStream, java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        OutputStream output, ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, params, output, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.io.File)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params, File localFile) {
        return CLIENT.download(httpMethod, url, params, localFile);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.io.File, java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params, File localFile,
        ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, params, localFile, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map, java.io.File)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, File localFile) {
        return CLIENT.download(httpMethod, url, params, headers, localFile);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map, java.io.File,
     *      java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, File localFile, ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, params, headers, localFile, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map, java.io.OutputStream)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output) {
        return CLIENT.download(httpMethod, url, params, headers, output);
    }

    /**
     * Request.
     *
     * @param requestBuilder the request builder
     * @return the string
     * @see cn.featherfly.jhttp.HttpClient#request(java.util.function.Function)
     */
    public static String request(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder) {
        return CLIENT.request(requestBuilder);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @param progress the progress
     * @return the long
     * @see cn.featherfly.jhttp.HttpDownloadClient#download(cn.featherfly.jhttp.HttpMethod,
     *      java.lang.String, java.util.Map, java.util.Map, java.io.OutputStream,
     *      java.util.function.BiConsumer)
     */
    public static Long download(HttpMethod httpMethod, String url, Map<String, Serializable> params,
        Map<String, String> headers, OutputStream output, ProgressConsumer progress) {
        return CLIENT.download(httpMethod, url, params, headers, output, progress);
    }

}
