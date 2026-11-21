package cn.featherfly.jhttp.jdk;

/**
 * Builder for the JDK 11 based async client.
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
        HttpAsyncClientImpl client = new HttpAsyncClientImpl(createClient(), defaultHeaders, serialization, contentType);
        applyCommon(client);
        return client;
    }
}
