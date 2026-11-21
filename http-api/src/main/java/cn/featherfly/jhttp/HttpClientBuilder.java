
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-21 14:56:21
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

import cn.featherfly.common.lang.ServiceLoaderUtils;

/**
 * HttpClientBuilder.
 *
 * @author zhongj
 */
public final class HttpClientBuilder {

    /**
     * HttpSyncClienBuilder.
     *
     * @return the http sync clien builder
     */
    public static HttpSyncClientBuilder sync() {
        return ServiceLoaderUtils.load(HttpSyncClientBuilder.class);
    }

    /**
     * HttpAsyncClienBuilder.
     *
     * @return the http async clien builder
     */
    public static HttpAsyncClientBuilder async() {
        return ServiceLoaderUtils.load(HttpAsyncClientBuilder.class);
    }

    /**
     * HttpRxjavaClienBuilder.
     *
     * @return the http rxjava clien builder
     */
    public static HttpRxjavaClientBuilder rxjava() {
        return ServiceLoaderUtils.load(HttpRxjavaClientBuilder.class);
    }

}
