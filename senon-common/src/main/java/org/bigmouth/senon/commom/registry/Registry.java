package org.bigmouth.senon.commom.registry;

public interface Registry<T> {

    void register(T service);

    void unregister(String id);
}
