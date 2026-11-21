package cn.featherfly.jhttp;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Class HttpResponse.
 *
 * @author zhongj
 */
public interface HttpResponse {

    /**
     * The HTTP status code.
     *
     * @return the status code
     */
    int getStatusCode();

    /**
     * Raw data from this response.
     *
     * @return the data
     */
    byte[] getData();

    /**
     * Response headers.
     *
     * @return the headers
     */
    Map<String, String> getHeaders();

    /**
     * Network roundtrip time in milliseconds.
     *
     * @return the network time
     */
    long getNetworkTime();

    /**
     * Network roundtrip time in timeUnit.
     *
     * @param timeUnit the time unit
     * @return the network time
     */
    default long getNetworkTime(TimeUnit timeUnit) {
        return timeUnit.convert(getNetworkTime(), TimeUnit.MILLISECONDS);
    }
}
