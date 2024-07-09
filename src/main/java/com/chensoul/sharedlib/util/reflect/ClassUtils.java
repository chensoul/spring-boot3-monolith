package com.chensoul.sharedlib.util.reflect;

import com.chensoul.sharedlib.util.FormatUtils;
import com.chensoul.sharedlib.util.lang.function.CheckedSupplier;
import static com.chensoul.sharedlib.util.lang.function.Streams.filterAll;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The utilities class of {@link Class}
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public class ClassUtils {
	/**
	 * Constant <code>SIMPLE_TYPES</code>
	 */
	public static final Set<Class<?>> SIMPLE_TYPES = Sets.newHashSet(
		Void.class,
		Boolean.class,
		Character.class,
		Byte.class,
		Short.class,
		Integer.class,
		Long.class,
		Float.class,
		Double.class,
		String.class,
		BigDecimal.class,
		BigInteger.class,
		Date.class,
		Object.class);

	/**
	 * Constant <code>PRIMITIVE_TYPES</code>
	 */
	public static final Set<Class<?>> PRIMITIVE_TYPES = Sets.newHashSet(
		Void.TYPE,
		Boolean.TYPE,
		Character.TYPE,
		Byte.TYPE,
		Short.TYPE,
		Integer.TYPE,
		Long.TYPE,
		Float.TYPE,
		Double.TYPE
	);

	/**
	 * <p>isPrimitive.</p>
	 *
	 * @param type a {@link Class} object
	 * @return a boolean
	 */
	public static boolean isPrimitive(Class<?> type) {
		return PRIMITIVE_TYPES.contains(type);
	}

	/**
	 * <p>isFinal.</p>
	 *
	 * @param type a {@link Class} object
	 * @return a boolean
	 */
	public static boolean isFinal(Class<?> type) {
		return type != null && Modifier.isFinal(type.getModifiers());
	}

	/**
	 * <p>isSimpleType.</p>
	 *
	 * @param type a {@link Class} object
	 * @return a boolean
	 */
	public static boolean isSimpleType(Class<?> type) {
		return SIMPLE_TYPES.contains(type);
	}

	/**
	 * <p>isAssignableFrom.</p>
	 *
	 * @param superType  a {@link Class} object
	 * @param targetType a {@link Class} object
	 * @return a boolean
	 */
	public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
		// any argument is null
		if (superType == null || targetType == null) {
			return false;
		}
		// equals
		if (Objects.equals(superType, targetType)) {
			return true;
		}
		// isAssignableFrom
		return superType.isAssignableFrom(targetType);
	}

	/**
	 * <p>getAllInheritedTypes.</p>
	 *
	 * @param type        a {@link Class} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Class<?>> getAllInheritedTypes(Class<?> type, Predicate<Class<?>>... typeFilters) {
		// Add all super classes
		Set<Class<?>> types = new LinkedHashSet<>(getAllSuperClasses(type, typeFilters));
		// Add all interface classes
		types.addAll(getAllInterfaces(type, typeFilters));
		return unmodifiableSet(types);
	}

	/**
	 * <p>getAllInterfaces.</p>
	 *
	 * @param type             a {@link Class} object
	 * @param interfaceFilters a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Class<?>> getAllInterfaces(Class<?> type, Predicate<Class<?>>... interfaceFilters) {
		if (type == null || type.isPrimitive()) {
			return emptySet();
		}

		Set<Class<?>> allInterfaces = new LinkedHashSet<>();
		Set<Class<?>> resolved = new LinkedHashSet<>();
		Queue<Class<?>> waitResolve = new LinkedList<>();

		resolved.add(type);
		Class<?> clazz = type;
		while (clazz != null) {

			Class<?>[] interfaces = clazz.getInterfaces();

			if (ArrayUtils.isNotEmpty(interfaces)) {
				// add current interfaces
				Arrays.stream(interfaces).filter(resolved::add).forEach(cls -> {
					allInterfaces.add(cls);
					waitResolve.add(cls);
				});
			}

			// add all super classes to waitResolve
			getAllSuperClasses(clazz).stream().filter(resolved::add).forEach(waitResolve::add);

			clazz = waitResolve.poll();
		}

		return filterAll(allInterfaces, interfaceFilters);
	}

	/**
	 * <p>getAllSuperClasses.</p>
	 *
	 * @param type         a {@link Class} object
	 * @param classFilters a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Class<?>> getAllSuperClasses(Class<?> type, Predicate<Class<?>>... classFilters) {

		Set<Class<?>> allSuperClasses = new LinkedHashSet<>();

		Class<?> superClass = type.getSuperclass();
		while (superClass != null) {
			// add current super class
			allSuperClasses.add(superClass);
			superClass = superClass.getSuperclass();
		}

		return unmodifiableSet(filterAll(allSuperClasses, classFilters));
	}

	/**
	 * <p>newInstance.</p>
	 *
	 * @param type a {@link Class} object
	 * @param args a {@link Object} object
	 * @param <T>  a T class
	 * @return a T object
	 */
	public static <T> T newInstance(Class<T> type, Object... args) {
		int length = ArrayUtils.getLength(args);

		List<Constructor<?>> constructors = ConstructorUtils.getDeclaredConstructors(type, constructor -> {
			Class<?>[] parameterTypes = constructor.getParameterTypes();
			if (length != parameterTypes.length) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				Object arg = args[i];
				Class<?> parameterType = parameterTypes[i];
				if (!parameterType.isInstance(arg)) {
					return false;
				}
			}
			return true;
		});

		if (constructors.isEmpty()) {
			String message = FormatUtils.format("No constructor[class : '{}'] matches the arguments : {}", getTypeName(type), Arrays.asList(args));
			throw new IllegalArgumentException(message);
		}

		Constructor<T> constructor = (Constructor<T>) constructors.get(0);
		return CheckedSupplier.unchecked(() -> constructor.newInstance(args)).get();
	}

	/**
	 * <p>getTypes.</p>
	 *
	 * @param values a {@link Object} object
	 * @return an array of {@link Class} objects
	 */
	public static Class[] getTypes(Object... values) {

		if (ArrayUtils.isEmpty(values)) {
			return ArrayUtils.EMPTY_CLASS_ARRAY;
		}

		int size = values.length;

		Class[] types = new Class[size];

		for (int i = 0; i < size; i++) {
			Object value = values[i];
			types[i] = value == null ? null : value.getClass();
		}

		return types;
	}

	/**
	 * Get the name of the specified type
	 *
	 * @param type the specified type
	 * @return non-null
	 */
	public static String getTypeName(Class<?> type) {
		if (type.isArray()) {
			try {
				Class<?> cl = type;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				String name = getTypeName(cl);
				StringBuilder sb = new StringBuilder(name.length() + dimensions * 2);
				sb.append(name);
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) {
			}
		}
		return type.getName();
	}

	/**
	 * Get the simple name of the specified type
	 *
	 * @param type the specified type
	 * @return non-null
	 */
	public static String getSimpleName(Class<?> type) {
		boolean array = type.isArray();
		return getSimpleName(type, array);
	}

	private static String getSimpleName(Class<?> type, boolean array) {
		if (array) {
			return getSimpleName(type.getComponentType()) + "[]";
		}
		String simpleName = type.getName();
		Class<?> enclosingClass = type.getEnclosingClass();
		if (enclosingClass == null) { // top level class
			simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
		} else {
			String ecName = enclosingClass.getName();
			simpleName = simpleName.substring(ecName.length());
			// Remove leading "\$[0-9]*" from the name
			int length = simpleName.length();
			if (length < 1 || simpleName.charAt(0) != '$') throw new InternalError("Malformed class name");
			int index = 1;
			while (index < length && isAsciiDigit(simpleName.charAt(index))) index++;
			// Eventually, this is the empty string iff this is an anonymous class
			return simpleName.substring(index);
		}
		return simpleName;
	}

	private static boolean isAsciiDigit(char c) {
		return '0' <= c && c <= '9';
	}
}
