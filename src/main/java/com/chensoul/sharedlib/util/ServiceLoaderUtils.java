package com.chensoul.sharedlib.util;

import com.chensoul.sharedlib.util.lang.Prioritized;
import com.chensoul.sharedlib.util.reflect.ClassLoaderUtils;
import static com.chensoul.sharedlib.util.reflect.ReflectionUtils.toList;
import com.google.common.collect.Maps;
import static java.lang.Boolean.getBoolean;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public abstract class ServiceLoaderUtils {

	private static final Map<ClassLoader, Map<Class<?>, ServiceLoader<?>>> serviceLoadersCache = new ConcurrentHashMap<>();

	private static final boolean serviceLoaderCached = getBoolean("chensoul.service-loader.cached");

	static {
		// Clear cache on JVM shutdown
		ShutdownHookUtils.addShutdownHookCallback(serviceLoadersCache::clear);
	}

	public static <S> List<S> loadServicesList(Class<S> serviceType) throws IllegalArgumentException {
		return loadServicesList(serviceType, ClassLoaderUtils.getClassLoader(serviceType));
	}


	/**
	 * Using the hierarchy of {@link ClassLoader}, each level of ClassLoader ( ClassLoader , its parent ClassLoader and higher)
	 * will be able to load the config file META-INF/services <code>serviceType</code> under its class path.
	 * The config file of each service type can define multiple lists of implementation classes.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader {@link ClassLoader}
	 * @return service type all implementation objects of {@link java.util.Collections#unmodifiableList(List) readonly list}
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> List<S> loadServicesList(Class<S> serviceType, ClassLoader classLoader) throws IllegalArgumentException {
		return loadServicesList(serviceType, classLoader, serviceLoaderCached);
	}

	/**
	 * Using the hierarchy of {@link ClassLoader}, each level of ClassLoader ( ClassLoader , its parent ClassLoader and higher)
	 * will be able to load the config file META-INF/services <code>serviceType</code> under its class path.
	 * The config file of each service type can define multiple lists of implementation classes.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param cached      the list of services to be cached
	 * @return service type all implementation objects of {@link java.util.Collections#unmodifiableList(List) readonly list}
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> List<S> loadServicesList(Class<S> serviceType, boolean cached) throws IllegalArgumentException {
		return loadServicesList(serviceType, ClassLoaderUtils.getClassLoader(serviceType), cached);
	}


	/**
	 * Using the hierarchy of {@link ClassLoader}, each level of ClassLoader ( ClassLoader , its parent ClassLoader and higher)
	 * will be able to load the config file META-INF/services <code>serviceType</code> under its class path.
	 * The config file of each service type can define multiple lists of implementation classes.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader {@link ClassLoader}
	 * @param cached      the list of services to be cached
	 * @return service type all implementation objects of {@link java.util.Collections#unmodifiableList(List) readonly list}
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> List<S> loadServicesList(Class<S> serviceType, ClassLoader classLoader, boolean cached) throws IllegalArgumentException {
		return unmodifiableList(loadServicesList0(serviceType, classLoader, cached));
	}

	/**
	 * Load the first instance of {@link #loadServicesList(Class) Service interface instances list}
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, each level of ClassLoader will be able to access the config files under its class path
	 * /META-INF/services/<code>serviceType</code>.
	 * Then, override the first implementation class of the config file under the class path of ClassLoader,
	 * thereby providing a mechanism for overriding the implementation class.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @return If it exists, {@link #loadServicesList(Class, ClassLoader) loads the first in the list of implementation objects of service type}.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadFirstService(Class<S> serviceType) throws IllegalArgumentException {
		return loadFirstService(serviceType, ClassLoaderUtils.getClassLoader(serviceType));
	}

	/**
	 * Load the first instance of {@link #loadServicesList(Class) Service interface instances list}
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, each level of ClassLoader will be able to access the config files under its class path
	 * /META-INF/services/<code>serviceType</code>.
	 * Then, override the first implementation class of the config file under the class path of ClassLoader,
	 * thereby providing a mechanism for overriding the implementation class.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param cached      the list of services to be cached
	 * @return If it exists, {@link #loadServicesList(Class, ClassLoader) loads the first in the list of implementation objects of service type}.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadFirstService(Class<S> serviceType, boolean cached) throws IllegalArgumentException {
		return loadFirstService(serviceType, ClassLoaderUtils.getClassLoader(serviceType), cached);
	}

	/**
	 * Load the first instance of {@link #loadServicesList(Class, ClassLoader) Service interface instances list}
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, each level of ClassLoader will be able to access the config files under its class path
	 * /META-INF/services/<code>serviceType</code>.
	 * Then, override the first implementation class of the config file under the class path of ClassLoader,
	 * thereby providing a mechanism for overriding the implementation class.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader a {@link ClassLoader} object
	 * @return If it exists, {@link #loadServicesList(Class, ClassLoader) loads the first in the list of implementation objects of service type}.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadFirstService(Class<S> serviceType, ClassLoader classLoader) throws IllegalArgumentException {
		return loadFirstService(serviceType, classLoader, serviceLoaderCached);
	}

	/**
	 * Load the first instance of {@link #loadServicesList(Class, ClassLoader) Service interface instances list}
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, each level of ClassLoader will be able to access the config files under its class path
	 * /META-INF/services/<code>serviceType</code>.
	 * Then, override the first implementation class of the config file under the class path of ClassLoader,
	 * thereby providing a mechanism for overriding the implementation class.
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param cached      the list of services to be cached
	 * @param classLoader a {@link ClassLoader} object
	 * @return If it exists, {@link #loadServicesList(Class, ClassLoader) loads the first in the list of implementation objects of service type}.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadFirstService(Class<S> serviceType, ClassLoader classLoader, boolean cached) throws IllegalArgumentException {
		return loadService(serviceType, classLoader, cached, true);
	}

	/**
	 * Loads the last in the list of objects implementing the service type, if present.
	 * <p>
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, once the config file is loaded in the parent's ClassLoader at a higher level (here the highest-level ClassLoader is Bootstrap ClassLoader)
	 * /META-INF/services/<code>serviceType</code>
	 * If the last implementation class is used, the lower-level Class Loader will not be able to override the previous definition。
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @return Loads the last in the list of objects implementing the service type, if present.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadLastService(Class<S> serviceType) throws IllegalArgumentException {
		return loadLastService(serviceType, ClassLoaderUtils.getClassLoader(serviceType));
	}

	/**
	 * Loads the last in the list of objects implementing the service type, if present.
	 * <p>
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, once the config file is loaded in the parent's ClassLoader at a higher level (here the highest-level ClassLoader is Bootstrap ClassLoader)
	 * /META-INF/services/<code>serviceType</code>
	 * If the last implementation class is used, the lower-level Class Loader will not be able to override the previous definition。
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param cached      the list of services to be cached
	 * @return Loads the last in the list of objects implementing the service type, if present.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadLastService(Class<S> serviceType, boolean cached) throws IllegalArgumentException {
		return loadLastService(serviceType, ClassLoaderUtils.getClassLoader(serviceType), cached);
	}

	/**
	 * Loads the last in the list of objects implementing the service type, if present.
	 * <p>
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, once the config file is loaded in the parent's ClassLoader at a higher level (here the highest-level ClassLoader is Bootstrap ClassLoader)
	 * /META-INF/services/<code>serviceType</code>
	 * If the last implementation class is used, the lower-level Class Loader will not be able to override the previous definition。
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader {@link ClassLoader}
	 * @return Loads the last in the list of objects implementing the service type, if present.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadLastService(Class<S> serviceType, ClassLoader classLoader) throws IllegalArgumentException {
		return loadLastService(serviceType, classLoader, serviceLoaderCached);
	}

	/**
	 * Loads the last in the list of objects implementing the service type, if present.
	 * <p>
	 * <p>
	 * Design Purpose : Using the hierarchy of {@link ClassLoader}, once the config file is loaded in the parent's ClassLoader at a higher level (here the highest-level ClassLoader is Bootstrap ClassLoader)
	 * /META-INF/services/<code>serviceType</code>
	 * If the last implementation class is used, the lower-level Class Loader will not be able to override the previous definition。
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader a {@link ClassLoader} object
	 * @param cached      a boolean
	 * @return Loads the last in the list of objects implementing the service type, if present.
	 * @throws IllegalArgumentException if any.
	 */
	public static <S> S loadLastService(Class<S> serviceType, ClassLoader classLoader, boolean cached) throws IllegalArgumentException {
		return loadService(serviceType, classLoader, cached, false);
	}

	/**
	 * <p>load.</p>
	 *
	 * @param serviceType a {@link Class} object
	 * @param classLoader a {@link ClassLoader} object
	 * @param cached      a boolean
	 * @param <S>         a S class
	 * @return a {@link ServiceLoader} object
	 */
	public static <S> ServiceLoader<S> load(Class<S> serviceType, ClassLoader classLoader, boolean cached) {
		if (cached) {
			Map<Class<?>, ServiceLoader<?>> serviceLoadersMap =
				serviceLoadersCache.computeIfAbsent(classLoader, cl -> Maps.newConcurrentMap());
			return (ServiceLoader<S>) serviceLoadersMap.computeIfAbsent(serviceType, type ->
				ServiceLoader.load(serviceType, classLoader));
		}
		return ServiceLoader.load(serviceType, classLoader);
	}


	private static <S> S loadService(Class<S> serviceType, ClassLoader classLoader, boolean cached, boolean first) {
		List<S> serviceList = loadServicesList0(serviceType, classLoader, cached);
		int index = first ? 0 : serviceList.size() - 1;
		return serviceList.get(index);
	}

	/**
	 * Load all instances of service type
	 *
	 * @param <S>         service type
	 * @param serviceType service type
	 * @param classLoader {@link ClassLoader}
	 * @param cached      the list of services to be cached
	 * @return Load all instances of service type
	 */
	private static <S> List<S> loadServicesList0(Class<S> serviceType, ClassLoader classLoader, boolean cached) throws IllegalArgumentException {
		if (classLoader == null) {
			classLoader = ClassLoaderUtils.getDefaultClassLoader();
		}
		ServiceLoader<S> serviceLoader = load(serviceType, classLoader, cached);
		Iterator<S> iterator = serviceLoader.iterator();
		List<S> serviceList = toList(iterator);

		if (serviceList.isEmpty()) {
			String className = serviceType.getName();
			String message = String.format("No Service interface[type : %s] implementation was defined in service loader config file[/META-INF/services/%s] under ClassLoader[%s]", className, className, classLoader);
			IllegalArgumentException e = new IllegalArgumentException(message);
			throw e;
		}

		sort(serviceList, Prioritized.COMPARATOR);

		return serviceList;
	}

}
