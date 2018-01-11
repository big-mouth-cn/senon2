package org.bigmouth.senon.commom.selector;

import org.bigmouth.senon.commom.registry.Service;

import java.util.List;

/**
 * @author allen
 * @since 1.0.0
 */
public interface Selector {

    <T extends Service> T select(List<T> services);
}
