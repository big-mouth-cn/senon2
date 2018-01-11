package org.bigmouth.senon.spring.zk;

import org.bigmouth.senon.commom.zookeeper.ZkClientHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author allen
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ZkConfiguration.class)
public class ZkAutoConfiguration {

    @Autowired
    private ZkConfiguration configuration;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ZkClientHolder zkClientHolder() {
        return new ZkClientHolder(configuration.getAddress(), configuration.getSessionTimeout());
    }
}
