package org.bigmouth.senon.commom.registry;

import java.util.List;

public interface Registry<T> {

    void register(T service);

    void unregister(String id);

    List<T> getServices();
}
