package cn.featherfly.jhttp.jdk;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.featherfly.common.algorithm.AlgorithmException;
import cn.featherfly.common.algorithm.URL;
import cn.featherfly.common.serialization.Serialization;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpUtils;
import cn.featherfly.jhttp.UploadFile;

/**
 * Utilities for the JDK 11 HTTP client implementation.
 *
 * @author zhongj
 */
public final class JdkHttpUtils {

    /** The Constant STREAM_CONTENT_TYPE. */
    public static final String STREAM_CONTENT_TYPE = "application/octet-stream";

    /** The Constant HTML_CONTENT_TYPE. */
    public static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    /** The Constant JSON_CONTENT_TYPE. */
    public static final String JSON_CONTENT_TYPE = Serialization.MIME_TYPE_JSON + "; charset=utf-8";

    /** The Constant XML_CONTENT_TYPE. */
    public static final String XML_CONTENT_TYPE = Serialization.MIME_TYPE_XML + "; charset=utf-8";

    /** The Constant KRYO_CONTENT_TYPE. */
    public static final String KRYO_CONTENT_TYPE = Serialization.MIME_TYPE_KRYO + "; charset=utf-8";

    /** The Constant PROTOBUFF_CONTENT_TYPE. */
    public static final String PROTOBUFF_CONTENT_TYPE = Serialization.MIME_TYPE_PROTOBUFF + "; charset=utf-8";

    /** The Constant FORM_CONTENT_TYPE. */
    public static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=utf-8";

    private JdkHttpUtils() {
    }

    /**
     * A request body together with its content type.
     */
    public static class Body {

        /** The content bytes. */
        public final byte[] content;

        /** The content type. */
        public final String contentType;

        /**
         * Instantiates a new body.
         *
         * @param content the content
         * @param contentType the content type
         */
        public Body(byte[] content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }
    }

    /**
     * Creates a URI from the given url.
     *
     * @param url the url
     * @return the URI
     */
    public static URI uri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new HttpException(e);
        }
    }

    /**
     * Append params to an url.
     *
     * @param url the url
     * @param params the params
     * @return the url with params
     */
    public static String appendParam(String url, Map<String, Serializable> params) {
        return HttpUtils.appendParam(url, params);
    }

    /**
     * Append one param to an url.
     *
     * @param url the url
     * @param name the name
     * @param value the value
     * @return the url with param
     */
    public static String appendParam(String url, String name, Serializable value) {
        return HttpUtils.appendParam(url, name, value);
    }

    /**
     * To parameter string.
     *
     * @param params the params
     * @return the parameter string
     */
    public static String toParameString(Map<String, Serializable> params) {
        return HttpUtils.toParameString(params);
    }

    /**
     * To parameter string.
     *
     * @param name the name
     * @param value the value
     * @return the parameter string
     */
    public static String toParameString(String name, Serializable value) {
        return HttpUtils.toParameString(name, value);
    }

    /**
     * Gets the base type of a content-type value, e.g. application/json.
     *
     * @param contentType the content type
     * @return the base type
     */
    public static String baseType(String contentType) {
        if (contentType == null) {
            return STREAM_CONTENT_TYPE;
        }
        int index = contentType.indexOf(';');
        return (index > 0 ? contentType.substring(0, index) : contentType).trim();
    }

    /**
     * Gets the charset declared in a content-type value.
     *
     * @param contentType the content type
     * @return the charset, or UTF-8 when absent
     */
    public static Charset charsetOf(String contentType) {
        if (contentType != null) {
            int index = contentType.toLowerCase().indexOf("charset=");
            if (index >= 0) {
                String value = contentType.substring(index + 8).trim();
                int end = value.indexOf(';');
                if (end > 0) {
                    value = value.substring(0, end);
                }
                try {
                    return Charset.forName(value.replace("\"", ""));
                } catch (RuntimeException e) {
                    return StandardCharsets.UTF_8;
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * Adapts a content-type string to the http-api MediaType interface.
     *
     * @param contentType the content type
     * @return the media type
     */
    public static cn.featherfly.jhttp.MediaType create(String contentType) {
        final String base = baseType(contentType);
        final Charset charset = charsetOf(contentType);
        final int index = base.indexOf('/');
        return new cn.featherfly.jhttp.MediaType() {

            @Override
            public String getType() {
                return index > 0 ? base.substring(0, index) : base;
            }

            @Override
            public String getSubtype() {
                return index > 0 ? base.substring(index + 1) : "";
            }

            @Override
            public Charset getCharset() {
                return charset;
            }
        };
    }

    /**
     * Converts JDK HttpHeaders to a simple map.
     *
     * @param headers the JDK headers
     * @return the map
     */
    public static Map<String, String> headersToMap(Map<String, List<String>> headers) {
        Map<String, String> map = new HashMap<>();
        if (headers != null) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                List<String> values = entry.getValue();
                map.put(entry.getKey(), values == null ? "" : String.join(",", values));
            }
        }
        return map;
    }

    /**
     * Creates a form body.
     *
     * @param params the params
     * @return the body
     */
    public static Body createFormBody(Map<String, Serializable> params) {
        return new Body(toParameString(params == null ? new HashMap<>() : params).getBytes(StandardCharsets.UTF_8),
            FORM_CONTENT_TYPE);
    }

    /**
     * Creates a multipart body, supporting {@link UploadFile}.
     *
     * @param params the params
     * @return the body
     */
    public static Body createMultipartBody(Map<String, Serializable> params) {
        String boundary = "----EasyHttpBoundary" + UUID.randomUUID().toString().replace("-", "");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (params != null) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                write(out, "--" + boundary + "\r\n");
                Serializable value = entry.getValue();
                if (value instanceof UploadFile) {
                    UploadFile uploadFile = (UploadFile) value;
                    String filename = uploadFile.getFilename();
                    try {
                        filename = URL.encodeURL(filename);
                    } catch (AlgorithmException e) {
                        // keep the original filename
                    }
                    write(out, "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + filename
                        + "\"\r\n");
                    write(out, "Content-Type: " + uploadFile.getMediaType() + "\r\n\r\n");
                    out.write(uploadFile.getContent(), 0, uploadFile.getContent().length);
                    write(out, "\r\n");
                } else {
                    write(out, "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n");
                    write(out, "Content-Type: text/plain; charset=utf-8\r\n\r\n");
                    write(out, value == null ? "" : value.toString());
                    write(out, "\r\n");
                }
            }
        }
        write(out, "--" + boundary + "--\r\n");
        return new Body(out.toByteArray(), "multipart/form-data; boundary=" + boundary);
    }

    /**
     * Creates the request body, using multipart when an UploadFile is present.
     *
     * @param params the params
     * @return the body
     */
    public static Body createRequestBody(Map<String, Serializable> params) {
        if (params != null) {
            for (Serializable value : params.values()) {
                if (value instanceof UploadFile) {
                    return createMultipartBody(params);
                }
            }
        }
        return createFormBody(params);
    }

    private static void write(ByteArrayOutputStream out, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        out.write(bytes, 0, bytes.length);
    }
}
