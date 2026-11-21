
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-21 11:39:21
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

import java.nio.charset.Charset;

/**
 * MediaType.
 *
 * @author zhongj
 */
public interface MediaType {

    /**
     * Returns the high-level media type, such as "text", "image", "audio", "video", or
     * "application".
     *
     * @return the type
     */
    String getType();

    /**
     * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
     *
     * @return the subtype
     */
    String getSubtype();

    /**
     * Returns the charset of this media type, or null if this media type doesn't specify a charset.
     *
     * @return the charset
     */
    Charset getCharset();
}
