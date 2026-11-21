package cn.featherfly.jhttp.jdk;

/**
 * Builder for the JDK 11 based sync client.
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
        HttpSyncClientImpl client = new HttpSyncClientImpl(createClient(), defaultHeaders, listeners, serialization,
            contentType);
        applyCommon(client);
        return client;
    }
}
