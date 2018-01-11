package org.bigmouth.senon.scheduler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author allen
 * @since 1.0.0
 */
@Component
public class AppContextBeans implements ApplicationListener<ContextRefreshedEvent> {

    private static ApplicationContext context;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        if (null == context.getParent()) {
            AppContextBeans.context = context;
        }
    }

    public static <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }
}
