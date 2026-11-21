
/*
 * All rights Reserved, Designed By zhongj
 * @Description:
 * @author: zhongj
 * @date: 2026-11-21 20:33:21
 * @Copyright: 2026 www.featherfly.cn Inc. All rights reserved.
 */
package cn.featherfly.jhttp;

/**
 * HttpListenerRegistry.
 *
 * @author zhongj
 * @param <C> the generic type
 */
public interface HttpListenerRegistry<C extends HttpListenerRegistry<C>> {

    /**
     * add HttpListener listener.
     *
     * @param listeners the listeners
     * @return the c
     */
    @SuppressWarnings("unchecked")
    default C addListener(Iterable<HttpListener> listeners) {
        if (listeners != null) {
            for (HttpListener listener : listeners) {
                addListener(listener);
            }
        }
        return (C) this;
    }

    /**
     * add HttpListener listener.
     *
     * @param listeners the listeners
     * @return the c
     */
    @SuppressWarnings("unchecked")
    default C addListener(HttpListener... listeners) {
        if (listeners != null) {
            for (HttpListener listener : listeners) {
                addListener(listener);
            }
        }
        return (C) this;
    }

    /**
     * add HttpListener listener.
     *
     * @param listener the listener
     * @return the c
     */
    C addListener(HttpListener listener);

    /**
     * remove HttpListener listener.
     *
     * @param listener the listener
     * @return the c
     */
    C removeListener(HttpListener listener);

    /**
     * Removes the listener.
     *
     * @param listeners the listeners
     * @return the c
     */
    @SuppressWarnings("unchecked")
    default C removeListener(HttpListener... listeners) {
        if (listeners != null) {
            for (HttpListener listener : listeners) {
                removeListener(listener);
            }
        }
        return (C) this;
    }

    /**
     * Clear listeners.
     *
     * @return the c
     */
    C clearListeners();
}
