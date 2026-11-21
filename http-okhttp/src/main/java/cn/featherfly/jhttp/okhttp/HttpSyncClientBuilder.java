package cn.featherfly.jhttp.okhttp;

/**
 * Builder for the okhttp3 based sync client.
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
        HttpSyncClientImpl client = new HttpSyncClientImpl(createClient(), defaultHeaders, serialization, mediaType);
        applyCommon(client);
        return client;
    }
}
