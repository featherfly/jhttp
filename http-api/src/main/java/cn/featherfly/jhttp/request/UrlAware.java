
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-07 16:36:07
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp.request;

/**
 * UrlAware.
 *
 * @author zhongj
 */
public interface UrlAware {

    /**
     * Gets the.
     *
     * @return the url method aware
     */
    UrlMethodAware get();

    /**
     * Head.
     *
     * @return the url method aware
     */
    UrlMethodAware head();

    /**
     * Delete.
     *
     * @return the url method aware
     */
    UrlMethodAware delete();

    /**
     * Post.
     *
     * @return the url form method aware
     */
    UrlFormMethodAware post();

    /**
     * Put.
     *
     * @return the url form method aware
     */
    UrlFormMethodAware put();

    /**
     * Patch.
     *
     * @return the url form method aware
     */
    UrlFormMethodAware patch();
}
