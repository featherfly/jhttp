package cn.featherfly.jhttp.httpclient5;

/**
 * Builder for the httpclient5 based async client.
 *
 * @author zhongj
 */
public class HttpAsyncClientBuilder extends AbstractHttpClientBuilder<HttpAsyncClientBuilder>
    implements cn.featherfly.jhttp.HttpAsyncClientBuilder {

    /**
     * Instantiates a new http async client builder.
     */
    public HttpAsyncClientBuilder() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpAsyncClientImpl build() {
        prepareToBuild();
        HttpAsyncClientImpl client = new HttpAsyncClientImpl(buildClassicClient(), buildAsyncClient(), defaultHeaders,
            listeners, serialization, contentType);
        applyCommon(client);
        return client;
    }
}
