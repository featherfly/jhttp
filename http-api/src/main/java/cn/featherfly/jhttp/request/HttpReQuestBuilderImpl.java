
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-07 19:00:07
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp.request;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import cn.featherfly.common.lang.ArrayUtils;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpListener;
import cn.featherfly.jhttp.HttpMethod;

/**
 * HttpReQuestBuilderImpl.
 *
 * @author zhongj
 */
public class HttpReQuestBuilderImpl implements HttpRequestBuilder, UrlAware {

    private final HttpRequest httpRequest;

    /**
     * Instantiates a new http re quest builder impl.
     */
    private HttpReQuestBuilderImpl() {
        httpRequest = new HttpRequest();
    }

    /**
     * Instantiates a new http re quest builder impl.
     *
     * @param listeners the listeners
     */
    public HttpReQuestBuilderImpl(Collection<HttpListener> listeners) {
        httpRequest = new HttpRequest();
        httpRequest.getListeners().addAll(listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlAware url(String url) {
        httpRequest.setUrl(url);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlMethodAware get() {
        httpRequest.setMethod(HttpMethod.GET);
        return new UrlMethodAwareImpl(httpRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlMethodAware head() {
        httpRequest.setMethod(HttpMethod.HEAD);
        return new UrlMethodAwareImpl(httpRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlMethodAware delete() {
        httpRequest.setMethod(HttpMethod.DELETE);
        return new UrlMethodAwareImpl(httpRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlFormMethodAware post() {
        httpRequest.setMethod(HttpMethod.POST);
        return new UrlFormMethodAwareImpl(httpRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlFormMethodAware put() {
        httpRequest.setMethod(HttpMethod.PUT);
        return new UrlFormMethodAwareImpl(httpRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UrlFormMethodAware patch() {
        httpRequest.setMethod(HttpMethod.PATCH);
        return new UrlFormMethodAwareImpl(httpRequest);
    }

    /**
     * The Class UrlMethodAwareImpl.
     */
    public static class UrlMethodAwareImpl implements UrlMethodAware, UrlMethodHeaderAware {

        private HttpRequest httpRequest;

        /**
         * Instantiates a new url method aware impl.
         *
         * @param httpRequest the http request
         */
        public UrlMethodAwareImpl(HttpRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlMethodHeaderAware header(Map<String, String> headers) {
            httpRequest.setHeaders(headers);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlMethodHeaderAware header(Consumer<HeaderBuilder> consumerHeader) {
            consumerHeader.accept(new HeaderBuilderImpl(httpRequest));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware parameter(Map<String, Serializable> parameters) {
            httpRequest.setParameters(parameters);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware parameter(Consumer<ParameterBuilder> parameterBuilder) {
            parameterBuilder.accept(new ParameterBuilderImpl(httpRequest));
            return this;
        }

    }

    /**
     * The Class UrlFormMethodAwareImpl.
     */
    public static class UrlFormMethodAwareImpl implements UrlFormMethodAware, UrlFormMethodHeaderAware {

        private HttpRequest httpRequest;

        /**
         * Instantiates a new url form method aware impl.
         *
         * @param httpRequest the http request
         */
        public UrlFormMethodAwareImpl(HttpRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlFormMethodHeaderAware header(Map<String, String> headers) {
            httpRequest.setHeaders(headers);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlFormMethodHeaderAware header(Consumer<HeaderBuilder> consumerHeader) {
            consumerHeader.accept(new HeaderBuilderImpl(httpRequest));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware parameter(Map<String, Serializable> parameters) {
            httpRequest.setParameters(parameters);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware parameter(Object body) {
            httpRequest.setRequestBody(body);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware parameter(Consumer<FormParameterBuilder> parameterBuilder) {
            parameterBuilder.accept(new FormParameterBuilderImpl(httpRequest));
            return this;
        }
    }

    /**
     * The Class HeaderBuilderImpl.
     */
    public static class HeaderBuilderImpl implements HeaderBuilder {

        private HttpRequest httpRequest;

        /**
         * Instantiates a new header builder impl.
         *
         * @param httpRequest the http request
         */
        public HeaderBuilderImpl(HttpRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        private String headerValue(String... values) {
            return String.join(",", values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HeaderBuilder set(String name, String... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getHeaders().put(name, headerValue(values));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HeaderBuilder add(String name, String... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getHeaders().compute(name, (k, v) -> {
                if (v == null) {
                    return headerValue(values);
                } else {
                    return headerValue(v, headerValue(values));
                }
            });
            return this;
        }
    }

    /**
     * The Class ParameterBuilderImpl.
     *
     * @author zhongj
     */
    public static class ParameterBuilderImpl implements ParameterBuilder {

        private HttpRequest httpRequest;

        /**
         * Instantiates a new parameter builder impl.
         *
         * @param httpRequest the http request
         */
        public ParameterBuilderImpl(HttpRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ParameterBuilder set(String name, Serializable... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getParameters().put(name, values);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ParameterBuilder add(String name, Serializable... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getParameters().compute(name, (k, v) -> {
                if (v == null) {
                    return values;
                } else {
                    return (Serializable) ArrayUtils.concat(v, values);
                }
            });
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequest getHttpRequest() {
            return httpRequest;
        }
    }

    /**
     * The Class FormParameterBuilderImpl.
     */
    public static class FormParameterBuilderImpl implements FormParameterBuilder {

        private HttpRequest httpRequest;

        /**
         * Instantiates a new form parameter builder impl.
         *
         * @param httpRequest the http request
         */
        public FormParameterBuilderImpl(HttpRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FormParameterBuilder set(String name, Serializable... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getParameters().put(name, values);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FormParameterBuilder add(String name, Serializable... values) {
            if (values == null) {
                return this;
            }
            httpRequest.getParameters().compute(name, (k, v) -> {
                if (v == null) {
                    return values;
                } else {
                    return (Serializable) ArrayUtils.concat(v, values);
                }
            });
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestAware body(Object body) {
            httpRequest.setRequestBody(body);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

    }

    /**
     * The Class BasicMethodHttpRequestBuilderImpl.
     */
    public static class BasicMethodHttpRequestBuilderImpl implements BasicMethodHttpRequestBuilder {

        private final HttpMethod method;

        private final Set<HttpListener> listeners = new LinkedHashSet<>(0);

        /**
         * Instantiates a new basic method http request builder impl.
         *
         * @param method the method
         * @param listeners the listeners
         */
        public BasicMethodHttpRequestBuilderImpl(HttpMethod method, Collection<HttpListener> listeners) {
            this.method = method;
            this.listeners.addAll(listeners);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlMethodAware url(String url) {
            HttpRequestBuilder requestBuilder = new HttpReQuestBuilderImpl();
            switch (method) {
                case GET:
                    return requestBuilder.addListener(listeners).url(url).get();
                case HEAD:
                    return requestBuilder.addListener(listeners).url(url).head();
                case DELETE:
                    return requestBuilder.addListener(listeners).url(url).delete();
                default:
                    throw new HttpException("unsupport http method " + method);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlMethodAware> addListener(HttpListener listener) {
            listeners.add(listener);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlMethodAware> removeListener(HttpListener listener) {
            listeners.remove(listener);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlMethodAware> clearListeners() {
            listeners.clear();
            return this;
        }
    }

    /**
     * The Class FormMethodHttpRequestBuilderImpl.
     */
    public static class FormMethodHttpRequestBuilderImpl implements FormMethodHttpRequestBuilder {

        private final HttpMethod method;

        private final Set<HttpListener> listeners = new LinkedHashSet<>(0);

        /**
         * Instantiates a new form method http request builder impl.
         *
         * @param method the method
         * @param listeners the listeners
         */
        public FormMethodHttpRequestBuilderImpl(HttpMethod method, Collection<HttpListener> listeners) {
            this.method = method;
            this.listeners.addAll(listeners);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UrlFormMethodAware url(String url) {
            HttpRequestBuilder requestBuilder = new HttpReQuestBuilderImpl();
            switch (method) {
                case POST:
                    return requestBuilder.addListener(listeners).url(url).post();
                case PUT:
                    return requestBuilder.addListener(listeners).url(url).put();
                case PATCH:
                    return requestBuilder.addListener(listeners).url(url).patch();
                default:
                    throw new HttpException("unsupport http method " + method);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlFormMethodAware> addListener(HttpListener listener) {
            listeners.add(listener);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlFormMethodAware> removeListener(HttpListener listener) {
            listeners.remove(listener);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodAwareHttpBuilder<UrlFormMethodAware> clearListeners() {
            listeners.clear();
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestBuilder addListener(HttpListener listener) {
        httpRequest.getListeners().add(listener);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestBuilder removeListener(HttpListener listener) {
        httpRequest.getListeners().remove(listener);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequestBuilder clearListeners() {
        httpRequest.getListeners().clear();
        return this;
    }
}
