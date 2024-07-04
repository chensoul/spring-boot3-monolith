package com.chensoul.sharedlib.webmvc;

import com.chensoul.sharedlib.exception.BusinessException;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@Lazy(false)
public class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware {
	private static ConfigurableListableBeanFactory beanFactory;
	private static ApplicationContext applicationContext;

	public SpringContextHolder() {
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	public static ListableBeanFactory getBeanFactory() {
		return (ListableBeanFactory) (null == beanFactory ? applicationContext : beanFactory);
	}

	public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
		ConfigurableListableBeanFactory factory;
		if (null != beanFactory) {
			factory = beanFactory;
		} else {
			if (!(applicationContext instanceof ConfigurableApplicationContext)) {
				throw new BusinessException("No ConfigurableListableBeanFactory from context!");
			}

			factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
		}

		return factory;
	}

	public static <T> T getBean(String name) {
		return (T) getBeanFactory().getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return getBeanFactory().getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return getBeanFactory().getBean(name, clazz);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> type) {
		return getBeanFactory().getBeansOfType(type);
	}

	public static String[] getBeanNamesForType(Class<?> type) {
		return getBeanFactory().getBeanNamesForType(type);
	}

	public static String getProperty(String key) {
		return null == applicationContext ? null : applicationContext.getEnvironment().getProperty(key);
	}

	public static String getApplicationName() {
		return getProperty("spring.application.name");
	}

	public static String[] getActiveProfiles() {
		return null == applicationContext ? null : applicationContext.getEnvironment().getActiveProfiles();
	}

	public static String getActiveProfile() {
		String[] activeProfiles = getActiveProfiles();
		return ArrayUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
	}

	public static <T> void registerBean(String beanName, T bean) {
		ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
		factory.autowireBean(bean);
		factory.registerSingleton(beanName, bean);
	}

	public static void unregisterBean(String beanName) {
		ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
		if (factory instanceof DefaultSingletonBeanRegistry) {
			DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
			registry.destroySingleton(beanName);
		} else {
			throw new BusinessException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
		}
	}

	public static void publishEvent(ApplicationEvent event) {
		if (null != applicationContext) {
			applicationContext.publishEvent(event);
		}
	}

	public static void publishEvent(Object event) {
		if (null != applicationContext) {
			applicationContext.publishEvent(event);
		}
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SpringContextHolder.beanFactory = beanFactory;
	}
}
