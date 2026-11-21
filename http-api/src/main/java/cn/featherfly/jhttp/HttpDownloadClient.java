
/*
 * All rights Reserved, Designed By zhongj
 * @Title: Client.java
 * @Package cn.featherfly.common.http
 * @Description: Client
 * @author: zhongj
 * @date: 2022-11-07 17:25:07
 * @Copyright: 2022 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import cn.featherfly.common.io.FileUtils;
import cn.featherfly.common.lang.AssertIllegalArgument;
import cn.featherfly.jhttp.request.HttpRequestAware;
import cn.featherfly.jhttp.request.HttpRequestBuilder;
import io.reactivex.rxjava3.core.Observable;

/**
 * Client.
 *
 * @author zhongj
 * @param <D> the generic download response type
 */
public interface HttpDownloadClient<D> {

    /**
     * download.
     *
     * @param requestBuilder the request builder
     * @param output the output
     * @return the download size result
     */
    D download(Function<HttpRequestBuilder, HttpRequestAware> requestBuilder, OutputStream output);

    /**
     * Download.
     *
     * @param url the url
     * @param output the output
     * @return the download size result
     */
    default D download(String url, OutputStream output) {
        return download(HttpMethod.GET, url, output);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param output the output
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, OutputStream output) {
        return download(httpMethod, url, Collections.emptyMap(), output);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(String url, OutputStream output, ProgressConsumer progress) {
        return download(HttpMethod.GET, url, output, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, OutputStream output, ProgressConsumer progress) {
        return download(httpMethod, url, Collections.emptyMap(), output, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param localFile the local file
     * @return the download size result
     */
    default D download(String url, File localFile) {
        return download(HttpMethod.GET, url, localFile);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param localFile the local file
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, File localFile) {
        return download(httpMethod, url, Collections.emptyMap(), localFile);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(String url, File localFile, ProgressConsumer progress) {
        return download(HttpMethod.GET, url, localFile, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, File localFile, ProgressConsumer progress) {
        return download(httpMethod, url, Collections.emptyMap(), localFile, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param output the output
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, OutputStream output) {
        return download(url, params, Collections.emptyMap(), output);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param output the output
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, OutputStream output) {
        return download(httpMethod, url, params, Collections.emptyMap(), output);
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
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, OutputStream output, ProgressConsumer progress) {
        return download(url, params, Collections.emptyMap(), output, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param output the output
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, OutputStream output,
        ProgressConsumer progress) {
        return download(httpMethod, url, params, Collections.emptyMap(), output, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, File localFile) {
        return download(url, params, Collections.emptyMap(), localFile);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, File localFile) {
        return download(httpMethod, url, params, Collections.emptyMap(), localFile);
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
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, File localFile, ProgressConsumer progress) {
        return download(url, params, Collections.emptyMap(), localFile, progress);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param localFile the local file
     * @param progress the progress
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, File localFile,
        ProgressConsumer progress) {
        return download(httpMethod, url, params, Collections.emptyMap(), localFile, progress);
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, Map<String, String> headers, File localFile) {
        return download(url, params, headers, localFile, (r, t) -> {
        });
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param localFile the local file
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers,
        File localFile) {
        return download(httpMethod, url, params, headers, localFile, (r, t) -> {
        });
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
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, Map<String, String> headers, File localFile,
        ProgressConsumer progress) {
        return download(HttpMethod.GET, url, params, headers, localFile, progress);
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
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers,
        File localFile, ProgressConsumer progress) {
        AssertIllegalArgument.isNotNull(localFile, "localFile");
        FileUtils.makeDirectory(localFile);
        final OutputStream output;
        try {
            output = new FileOutputStream(localFile);
        } catch (FileNotFoundException e) {
            throw new HttpException(e);
        }
        D result;
        try {
            result = download(httpMethod, url, params, headers, output, progress);
        } catch (RuntimeException e) {
            closeQuietly(output);
            throw e;
        }
        return closeOutputWhenDone(result, output);
    }

    /**
     * Close the file output stream when the download result completes. A synchronous
     * result closes immediately; an async result closes on success, error or exception;
     * an rxjava result closes on emission, error or disposal.
     *
     * @param <D> the generic type
     * @param result the download result
     * @param output the file output stream
     * @return the same result (rxjava results are wrapped)
     */
    @SuppressWarnings("unchecked")
    static <D> D closeOutputWhenDone(D result, OutputStream output) {
        if (result instanceof HttpRequestCompletion) {
            Runnable close = () -> closeQuietly(output);
            HttpRequestCompletion<?> completion = (HttpRequestCompletion<?>) result;
            completion.completion(r -> close.run(), e -> close.run()).exceptionally(t -> {
                close.run();
                return null;
            });
            return result;
        }
        if (result instanceof Observable) {
            Observable<?> observable = (Observable<?>) result;
            return (D) observable.doOnNext(r -> closeQuietly(output)).doOnError(e -> closeQuietly(output))
                .doOnDispose(() -> closeQuietly(output));
        }
        closeQuietly(output);
        return result;
    }

    /**
     * Close the output stream, ignoring any close failure.
     *
     * @param output the output stream
     */
    static void closeQuietly(OutputStream output) {
        if (output == null) {
            return;
        }
        try {
            output.close();
        } catch (IOException e) {
            // nothing useful can be done here
        }
    }

    /**
     * Download.
     *
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, Map<String, String> headers, OutputStream output) {
        return download(HttpMethod.GET, url, params, headers, output);
    }

    /**
     * Download.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param params the params
     * @param headers the headers
     * @param output the output
     * @return the download size result
     */
    default D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers,
        OutputStream output) {
        return download(httpMethod, url, params, headers, output, (r, t) -> {
        });
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
     * @return the download size result
     */
    default D download(String url, Map<String, Serializable> params, Map<String, String> headers, OutputStream output,
        ProgressConsumer progress) {
        return download(HttpMethod.GET, url, params, headers, output, progress);
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
     *        argu0 readed bytes
     *        argu1 length
     * @return the download size result
     */
    D download(HttpMethod httpMethod, String url, Map<String, Serializable> params, Map<String, String> headers,
        OutputStream output, ProgressConsumer progress);

    @FunctionalInterface
    public interface ProgressConsumer {

        /**
         * Performs this operation on the given arguments.
         *
         * @param readedBytes the readed bytes
         * @param length the length
         */
        void accept(long readedBytes, long length);

        /**
         * Returns a composed {@code BiConsumer} that performs, in sequence, this
         * operation followed by the {@code after} operation. If performing either
         * operation throws an exception, it is relayed to the caller of the
         * composed operation. If performing this operation throws an exception,
         * the {@code after} operation will not be performed.
         *
         * @param after the operation to perform after this operation
         * @return a composed {@code BiConsumer} that performs in sequence this
         *         operation followed by the {@code after} operation
         * @throws NullPointerException if {@code after} is null
         */
        default ProgressConsumer andThen(ProgressConsumer after) {
            Objects.requireNonNull(after);

            return (l, r) -> {
                accept(l, r);
                after.accept(l, r);
            };
        }
    }
}
