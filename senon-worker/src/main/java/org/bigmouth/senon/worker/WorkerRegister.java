package org.bigmouth.senon.worker;

import org.apache.commons.lang.math.NumberUtils;
import org.bigmouth.senon.commom.registry.WorkerRegistry;
import org.bigmouth.senon.commom.utils.NetUtils;
import org.bigmouth.senon.commom.worker.Worker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author allen
 * @since 1.0.0
 */
@Component
public class WorkerRegister implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (null == applicationContext.getParent()) {
            WorkerRegistry registry = applicationContext.getBean(WorkerRegistry.class);

            Worker worker = new Worker();
            String property = applicationContext.getEnvironment().getProperty("server.port");
            worker.setHost(NetUtils.getLocalHost());
            worker.setPort(NumberUtils.toInt(property));

            registry.register(worker);
        }
    }
}
