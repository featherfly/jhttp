
package cn.featherfly.jhttp;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import cn.featherfly.common.constant.Chars;
import cn.featherfly.common.lang.Lang;

/**
 * http utils.
 *
 * @author zhongj
 */
public final class HttpUtils {

    private HttpUtils() {
    }

    /**
     * to parameter name value string. example: name=yufei&amp;age=1
     *
     * @param params the params
     * @return parameter name value string
     */
    public static String toParameString(Map<String, Serializable> params) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Serializable> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            sb.append("&").append(toParameString(entry.getKey(), entry.getValue()));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * to parameter name value string. example:name=yufei or name=yufei&amp;name=yi
     *
     * @param name the name
     * @param value the value
     * @return parameter name value string
     */
    public static String toParameString(String name, Serializable value) {
        StringBuilder sb = new StringBuilder();
        if (value == null) {
            return "";
        }
        Lang.eachObj(value, v -> sb.append("&").append(name).append("=").append(v));
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Append param.
     *
     * @param url the url
     * @param name the name
     * @param value the value
     * @return the url string with param
     */
    public static String appendParam(String url, String name, Serializable value) {
        if (Lang.isEmpty(url)) {
            return url;
        }
        String uri = url;
        if (uri.contains(Chars.QUESTION)) {
            uri += Chars.AMP;
        } else {
            uri += Chars.QUESTION;
        }
        uri += toParameString(name, value);
        return uri;
    }

    /**
     * Append param.
     *
     * @param url the url
     * @param params the params
     * @return the url string with params
     */
    public static String appendParam(String url, Map<String, Serializable> params) {
        if (Lang.isEmpty(params)) {
            return url;
        }

        if (url.contains(Chars.QUESTION)) {
            url += Chars.AMP;
        } else {
            url += Chars.QUESTION;
        }
        url += toParameString(params);
        return url;
    }
}
