
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-07 16:58:07
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;

/**
 * HttpRequest.
 *
 * @author zhongj
 */
public class HttpRequest {

    private String url;

    private HttpMethod method;

    private Map<String, String> headers = new HashMap<>(0);

    private Map<String, Serializable> parameters = new HashMap<>(0);

    private Object requestBody;

    // 请求会话最终使用的listeners,初始化的时候就拿到client的全局listeners
    private Set<HttpListener> listeners = new LinkedHashSet<>(0);

    /**
     * get url value
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set url value
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get headers value
     *
     * @return headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * set headers value
     *
     * @param headers headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * get parameters value
     *
     * @return parameters
     */
    public Map<String, Serializable> getParameters() {
        return parameters;
    }

    /**
     * set parameters value
     *
     * @param parameters parameters
     */
    public void setParameters(Map<String, Serializable> parameters) {
        this.parameters = parameters;
    }

    /**
     * get requestBody value
     *
     * @return requestBody
     */
    public Object getRequestBody() {
        return requestBody;
    }

    /**
     * set requestBody value
     *
     * @param requestBody requestBody
     */
    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * get method value
     *
     * @return method
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * set method value
     *
     * @param method method
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * get listeners value
     *
     * @return listeners
     */
    public Set<HttpListener> getListeners() {
        return listeners;
    }

    /**
     * set listeners value
     *
     * @param listeners listeners
     */
    public void setListeners(Set<HttpListener> listeners) {
        this.listeners = listeners;
    }
}
