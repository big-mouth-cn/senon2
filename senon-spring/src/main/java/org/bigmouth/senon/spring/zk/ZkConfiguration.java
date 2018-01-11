package org.bigmouth.senon.spring.zk;

import org.bigmouth.senon.commom.zookeeper.ZkClientHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author allen
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "senon.zk")
public class ZkConfiguration {

    private String address = "127.0.0.1:2181";
    private int sessionTimeout = ZkClientHolder.SESSION_TIMEOUT;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
