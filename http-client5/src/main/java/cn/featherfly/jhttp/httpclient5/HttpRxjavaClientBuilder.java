package cn.featherfly.jhttp.httpclient5;

/**
 * Builder for the httpclient5 based rxjava client.
 *
 * @author zhongj
 */
public class HttpRxjavaClientBuilder extends AbstractHttpClientBuilder<HttpRxjavaClientBuilder>
    implements cn.featherfly.jhttp.HttpRxjavaClientBuilder {

    private boolean autoSubscribeOnIo = true;

    /**
     * Instantiates a new http rxjava client builder.
     */
    public HttpRxjavaClientBuilder() {
    }

    /**
     * Set whether the returned observables subscribe on the IO scheduler.
     *
     * @param autoSubscribeOnIo true to enable
     * @return this builder
     */
    public HttpRxjavaClientBuilder autoSubscribeOnIo(boolean autoSubscribeOnIo) {
        this.autoSubscribeOnIo = autoSubscribeOnIo;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRxjavaClientImpl build() {
        prepareToBuild();
        HttpRxjavaClientImpl client = new HttpRxjavaClientImpl(buildClassicClient(), buildAsyncClient(), defaultHeaders,
            listeners, serialization, contentType);
        applyCommon(client);
        client.setAutoSubscribeOnIo(autoSubscribeOnIo);
        return client;
    }
}
