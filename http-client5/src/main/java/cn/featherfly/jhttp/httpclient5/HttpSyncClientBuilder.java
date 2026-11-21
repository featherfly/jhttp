package cn.featherfly.jhttp.httpclient5;

/**
 * Builder for the httpclient5 based sync client.
 *
 * @author zhongj
 */
public class HttpSyncClientBuilder extends AbstractHttpClientBuilder<HttpSyncClientBuilder>
    implements cn.featherfly.jhttp.HttpSyncClientBuilder {

    /**
     * Instantiates a new http sync client builder.
     */
    public HttpSyncClientBuilder() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpSyncClientImpl build() {
        prepareToBuild();
        HttpSyncClientImpl client = new HttpSyncClientImpl(buildClassicClient(), buildAsyncClient(), defaultHeaders,
            serialization, contentType);
        applyCommon(client);
        return client;
    }
}
