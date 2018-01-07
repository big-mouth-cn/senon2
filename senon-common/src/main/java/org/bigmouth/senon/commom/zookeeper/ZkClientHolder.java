package org.bigmouth.senon.commom.zookeeper;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.bigmouth.senon.commom.utils.BaseLifeCycleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZkClientHolder extends BaseLifeCycleSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClientHolder.class);
    
    public static final int MAX_RETRIES = 3;
    public static final int BASE_SLEEP_TIMEMS = 3000;
    public static final int SESSION_TIMEOUT = 60000;

    private CuratorFramework zkClient;

    private final String connectString;
    private final int sessionTimeout;
    
    public ZkClientHolder(String connectString) {
        this(connectString, SESSION_TIMEOUT);
    }

    public ZkClientHolder(String connectString, int sessionTimeout) {
        Preconditions.checkArgument(StringUtils.isNotBlank(connectString), "connectString cannot be blank");
        Preconditions.checkArgument(sessionTimeout >= 10000, "sessionTimeout must be greater than 10000");
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
    }
    
    public CuratorFramework get() {
        return zkClient;
    }

    @Override
    public void doInit() {
        zkClient = CuratorFrameworkFactory.builder()
                .sessionTimeoutMs(sessionTimeout)
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIMEMS, MAX_RETRIES))
                .build();
        zkClient.start();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Connected to ZooKepper server: {}", connectString);
        }
    }

    @Override
    public void doDestroy() {
        if (null != zkClient)
            zkClient.close();
    }
}
