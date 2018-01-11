package org.bigmouth.senon.spring.selector;

import org.bigmouth.senon.commom.selector.RandomSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author allen
 * @since 1.0.0
 */
@Configuration
public class RandomSelectorAutoConfiguration {

    @Bean
    public RandomSelector randomSelector() {
        return new RandomSelector();
    }
}
