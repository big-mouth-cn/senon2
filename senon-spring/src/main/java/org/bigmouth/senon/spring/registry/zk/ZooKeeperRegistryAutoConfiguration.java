package org.bigmouth.senon.spring.registry.zk;

import org.bigmouth.senon.commom.registry.zk.SchedulerZooKeeperRegistry;
import org.bigmouth.senon.commom.registry.zk.WorkerZooKeeperRegistry;
import org.bigmouth.senon.commom.zookeeper.ZkClientHolder;
import org.bigmouth.senon.spring.zk.ZkAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author allen
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(ZkAutoConfiguration.class)
public class ZooKeeperRegistryAutoConfiguration {

    @Bean
    public SchedulerZooKeeperRegistry schedulerZooKeeperRegistry(ZkClientHolder zkClientHolder) {
        return new SchedulerZooKeeperRegistry(zkClientHolder);
    }

    @Bean
    public WorkerZooKeeperRegistry workerZooKeeperRegistry(ZkClientHolder zkClientHolder) {
        return new WorkerZooKeeperRegistry(zkClientHolder);
    }
}
