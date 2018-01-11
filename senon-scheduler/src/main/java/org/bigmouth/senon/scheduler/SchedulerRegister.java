package org.bigmouth.senon.scheduler;

import org.apache.commons.lang.math.NumberUtils;
import org.bigmouth.senon.commom.registry.SchedulerRegistry;
import org.bigmouth.senon.commom.scheduler.Scheduler;
import org.bigmouth.senon.commom.utils.NetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author allen
 * @since 1.0.0
 */
@Component
public class SchedulerRegister implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (null == applicationContext.getParent()) {
            SchedulerRegistry registry = applicationContext.getBean(SchedulerRegistry.class);

            Scheduler scheduler = new Scheduler();
            String property = applicationContext.getEnvironment().getProperty("server.port");
            scheduler.setHost(NetUtils.getLocalHost());
            scheduler.setPort(NumberUtils.toInt(property));

            registry.register(scheduler);
        }
    }
}
