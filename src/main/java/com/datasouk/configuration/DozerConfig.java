package com.datasouk.configuration;

/**
 * @author kalaivani
 */
import org.dozer.DozerBeanMapper;
import org.dozer.config.BeanContainer;
import org.dozer.util.DefaultClassLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.dozer.DozerBeanMapper} configuration.
 */
@Configuration
public class DozerConfig {

    /**
     * Configures dozer mapper.
     *
     * @return the dozer bean mapper
     * @throws ReflectiveOperationException the reflective operation exception
     */
    @Bean
    public DozerBeanMapper mapper() throws ReflectiveOperationException {
        BeanContainer.getInstance().setClassLoader(new DefaultClassLoader(Thread.currentThread().getContextClassLoader()));
        final DozerBeanMapper mapper = new DozerBeanMapper();
        return mapper;
    }

}
