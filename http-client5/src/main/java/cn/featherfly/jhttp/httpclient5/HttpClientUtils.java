package cn.featherfly.jhttp.httpclient5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import cn.featherfly.common.algorithm.AlgorithmException;
import cn.featherfly.common.algorithm.URL;
import cn.featherfly.common.lang.Lang;
import cn.featherfly.common.serialization.Serialization;
import cn.featherfly.jhttp.HttpException;
import cn.featherfly.jhttp.HttpUtils;
import cn.featherfly.jhttp.UploadFile;

/**
 * http client5 utils.
 *
 * @author zhongj
 */
public final class HttpClientUtils {

    /** The Constant STREAM_CONTENT_TYPE. */
    public static final String STREAM_CONTENT_TYPE = "application/octet-stream";

    /** The Constant STREAM_CONTENT_TYPE_OBJ. */
    public static final ContentType STREAM_CONTENT_TYPE_OBJ = ContentType.APPLICATION_OCTET_STREAM;

    /** The Constant HTML_CONTENT_TYPE. */
    public static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    /** The Constant HTML_CONTENT_TYPE_OBJ. */
    public static final ContentType HTML_CONTENT_TYPE_OBJ = ContentType.create("text/html", StandardCharsets.UTF_8);

    /** The Constant JSON_CONTENT_TYPE. */
    public static final String JSON_CONTENT_TYPE = Serialization.MIME_TYPE_JSON + "; charset=utf-8";

    /** The Constant JSON_CONTENT_TYPE_OBJ. */
    public static final ContentType JSON_CONTENT_TYPE_OBJ = ContentType.create(Serialization.MIME_TYPE_JSON,
        StandardCharsets.UTF_8);

    /** The Constant XML_CONTENT_TYPE. */
    public static final String XML_CONTENT_TYPE = Serialization.MIME_TYPE_XML + "; charset=utf-8";

    /** The Constant XML_CONTENT_TYPE_OBJ. */
    public static final ContentType XML_CONTENT_TYPE_OBJ = ContentType.create(Serialization.MIME_TYPE_XML,
        StandardCharsets.UTF_8);

    /** The Constant KRYO_CONTENT_TYPE. */
    public static final String KRYO_CONTENT_TYPE = Serialization.MIME_TYPE_KRYO + "; charset=utf-8";

    /** The Constant KRYO_CONTENT_TYPE_OBJ. */
    public static final ContentType KRYO_CONTENT_TYPE_OBJ = ContentType.create(Serialization.MIME_TYPE_KRYO,
        StandardCharsets.UTF_8);

    /** The Constant PROTOBUFF_CONTENT_TYPE. */
    public static final String PROTOBUFF_CONTENT_TYPE = Serialization.MIME_TYPE_PROTOBUFF + "; charset=utf-8";

    /** The Constant PROTOBUFF_CONTENT_TYPE_OBJ. */
    public static final ContentType PROTOBUFF_CONTENT_TYPE_OBJ = ContentType.create(Serialization.MIME_TYPE_PROTOBUFF,
        StandardCharsets.UTF_8);

    private HttpClientUtils() {
    }

    /**
     * Creates the form entity.
     *
     * @param params the params
     * @return the url encoded form entity
     */
    public static UrlEncodedFormEntity createFormEntity(Map<String, Serializable> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        if (Lang.isNotEmpty(params)) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
        }
        return new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
    }

    /**
     * Creates the multipart entity, support upload file.
     *
     * @param params the params
     * @return the http entity
     */
    public static HttpEntity createMultipartEntity(Map<String, Serializable> params) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, Serializable> entry : params.entrySet()) {
            Serializable value = entry.getValue();
            if (value instanceof UploadFile) {
                UploadFile uploadFile = (UploadFile) value;
                String filename = uploadFile.getFilename();
                try {
                    filename = URL.encodeURL(filename);
                } catch (AlgorithmException e) {
                    // keep the original filename
                }
                builder.addBinaryBody(entry.getKey(), uploadFile.getContent(),
                    ContentType.parse(uploadFile.getMediaType()), filename);
            } else {
                builder.addTextBody(entry.getKey(), value == null ? "" : value.toString(), ContentType.TEXT_PLAIN);
            }
        }
        return builder.build();
    }

    /**
     * Creates the request body, support upload file.
     *
     * @param params the params
     * @return the http entity
     */
    public static HttpEntity createRequestBody(Map<String, Serializable> params) {
        boolean isMultipart = false;
        if (Lang.isNotEmpty(params)) {
            for (Serializable value : params.values()) {
                if (value instanceof UploadFile) {
                    isMultipart = true;
                    break;
                }
            }
        }
        if (isMultipart) {
            return createMultipartEntity(params);
        }
        return createFormEntity(params);
    }

    /**
     * Set the form or multipart body on an async request builder.
     *
     * @param builder the request builder
     * @param params the params
     */
    public static void setFormBody(SimpleRequestBuilder builder, Map<String, Serializable> params) {
        try {
            HttpEntity entity = createRequestBody(params);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            entity.writeTo(out);
            builder.setBody(out.toByteArray(), ContentType.parse(entity.getContentType()));
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /**
     * Apply headers to request, default headers first, request headers override.
     *
     * @param request the request
     * @param headers the headers
     */
    public static void applyHeaders(HttpUriRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Headers to map.
     *
     * @param headers the headers
     * @return the map
     */
    public static Map<String, String> headersToMap(Header[] headers) {
        Map<String, String> map = new HashMap<>();
        if (headers != null) {
            for (Header header : headers) {
                map.put(header.getName(), header.getValue());
            }
        }
        return map;
    }

    /**
     * to parameter name value string. example: name=yufei&amp;age=1
     *
     * @param params the params
     * @return the parameter string
     */
    public static String toParameString(Map<String, Serializable> params) {
        return HttpUtils.toParameString(params);
    }

    /**
     * to parameter name value string. example:name=yufei
     *
     * @param name the name
     * @param value the value
     * @return the parameter string
     */
    public static String toParameString(String name, Serializable value) {
        return HttpUtils.toParameString(name, value);
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
        return HttpUtils.appendParam(url, name, value);
    }

    /**
     * Append param.
     *
     * @param url the url
     * @param params the params
     * @return the url string with params
     */
    public static String appendParam(String url, Map<String, Serializable> params) {
        return HttpUtils.appendParam(url, params);
    }

    /**
     * Creates the cn.featherfly.http media type.
     *
     * @param contentType the content type
     * @return the media type
     */
    public static cn.featherfly.jhttp.MediaType create(ContentType contentType) {
        final String mimeType = contentType.getMimeType();
        final int index = mimeType.indexOf('/');
        final Charset charset = contentType.getCharset();
        return new cn.featherfly.jhttp.MediaType() {

            @Override
            public String getType() {
                return index > 0 ? mimeType.substring(0, index) : mimeType;
            }

            @Override
            public String getSubtype() {
                return index > 0 ? mimeType.substring(index + 1) : "";
            }

            @Override
            public Charset getCharset() {
                return charset;
            }
        };
    }
}
