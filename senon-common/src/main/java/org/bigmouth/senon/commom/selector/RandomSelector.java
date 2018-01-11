package org.bigmouth.senon.commom.selector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bigmouth.senon.commom.registry.Service;

import java.util.List;

/**
 * @author allen
 * @since 1.0.0
 */
public class RandomSelector implements Selector {

    @Override
    public <T extends Service> T select(List<T> services) {
        if (CollectionUtils.isEmpty(services))
            return null;
        return services.get(RandomUtils.nextInt(services.size()));
    }
}

