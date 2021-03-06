package org.bigmouth.senon.commom.registry.zk;

import com.google.common.collect.Lists;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.bigmouth.senon.commom.registry.RegistryException;
import org.bigmouth.senon.commom.registry.SchedulerRegistry;
import org.bigmouth.senon.commom.scheduler.Scheduler;
import org.bigmouth.senon.commom.utils.JsonHelper;
import org.bigmouth.senon.commom.worker.Worker;
import org.bigmouth.senon.commom.zookeeper.ZkClientHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class SchedulerZooKeeperRegistry implements SchedulerRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerZooKeeperRegistry.class);
    private static final String PATH_PARENT = "/senon/schedulers";
    private static final String PATH_PREFIX = ZKPaths.makePath(PATH_PARENT, "/scheduler_");
    private final ZkClientHolder zk;

    public SchedulerZooKeeperRegistry(ZkClientHolder zk) {
        this.zk = zk;
    }

    @Override
    public void register(Scheduler service) {
        service.setRegisterTime(new Date());

        try {
            String path = zk.get().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PATH_PREFIX, JsonHelper.convert2bytes(service));
            List<String> split = ZKPaths.split(path);
            service.setId(split.get(split.size() - 1));

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Register scheduler {} to {}", service, path);
            }
        } catch (Exception e) {
            throw new RegistryException("register: ", e);
        }
    }

    @Override
    public void unregister(String id) {
        try {
            String path = PATH_PREFIX + id;
            zk.get().delete().forPath(path);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Unregister scheduler {} from {}", id, path);
            }
        } catch (Exception e) {
            throw new RegistryException("unregister: ", e);
        }
    }

    @Override
    public List<Scheduler> getServices() {
        try {
            List<Scheduler> schedulers = Lists.newArrayList();
            List<String> children = zk.get().getChildren().forPath(PATH_PARENT);
            for (String child : children) {
                byte[] bytes = zk.get().getData().forPath(ZKPaths.makePath(PATH_PARENT, child));
                schedulers.add(JsonHelper.convert(bytes, Scheduler.class));
            }
            return schedulers;
        } catch (Exception e) {
            throw new RegistryException("getServices: ", e);
        }
    }
}
