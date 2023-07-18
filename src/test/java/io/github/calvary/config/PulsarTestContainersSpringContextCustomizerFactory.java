package io.github.calvary.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

public class PulsarTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private Logger log = LoggerFactory.getLogger(PulsarTestContainersSpringContextCustomizerFactory.class);

    private static PulsarTestContainer pulsarBean;

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        return (context, mergedConfig) -> {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            TestPropertyValues testValues = TestPropertyValues.empty();
            EmbeddedPulsar pulsarAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedPulsar.class);
            if (null != pulsarAnnotation) {
                log.debug("detected the EmbeddedPulsar annotation on class {}", testClass.getName());
                log.info("Warming up the pulsar database");
                if (null == pulsarBean) {
                    pulsarBean = beanFactory.createBean(PulsarTestContainer.class);
                    beanFactory.registerSingleton(PulsarTestContainer.class.getName(), pulsarBean);
                    // ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(PulsarTestContainer.class.getName(), pulsarBean);
                }
                testValues =
                    testValues.and(
                        "spring.pulsar.client.service-url=" + pulsarBean.getPulsarContainer().getPulsarBrokerUrl(),
                        "spring.pulsar.administration.service-url=" + pulsarBean.getPulsarContainer().getHttpServiceUrl()
                    );
            }
            testValues.applyTo(context);
        };
    }
}
