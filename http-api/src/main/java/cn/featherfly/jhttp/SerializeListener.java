
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-05-12 15:50:12
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

/**
 * SerializeListener.
 *
 * @author zhongj
 */
public interface SerializeListener {

    /**
     * On serialize.
     *
     * @param requestBody the request body
     * @param serializeBody the serialize body
     * @param mediaType the media type
     */
    void onSerialize(Object requestBody, byte[] serializeBody, MediaType mediaType);
}
