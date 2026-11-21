package cn.featherfly.jhttp;

/**
 * The Class HttpErrorResponse.
 *
 * @author zhongj
 */
public class HttpErrorResponse implements Error {

    private String message;

    private HttpResponse httpResponse;

    /**
     * Instantiates a new http error response.
     *
     * @param message the message
     */
    public HttpErrorResponse(String message) {
        this.message = message;
    }

    /**
     * Instantiates a new http error response.
     *
     * @param message the message
     * @param httpResponse the http response
     */
    public HttpErrorResponse(String message, HttpResponse httpResponse) {
        super();
        this.message = message;
        this.httpResponse = httpResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets the http response.
     *
     * @return the http response
     */
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
