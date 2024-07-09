package com.chensoul.sharedlib.util.reflect;

import static com.chensoul.sharedlib.util.StringPool.COMMA_CHAR;
import static com.chensoul.sharedlib.util.StringPool.LEFT_PARENTHESIS_CHAR;
import static com.chensoul.sharedlib.util.StringPool.RIGHT_PARENTHESIS_CHAR;
import static com.chensoul.sharedlib.util.StringPool.SHARP_CHAR;
import com.chensoul.sharedlib.util.lang.function.Streams;
import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_CLASS_ARRAY;

/**
 * The Java Reflection {@link Method} Utility class
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public abstract class MethodUtils {
	public final static Predicate<? super Method> OBJECT_METHOD_PREDICATE = MethodUtils::isObjectMethod;

	/**
	 * Constant <code>OBJECT_METHODS</code>
	 */
	public final static Set<Method> OBJECT_METHODS = Sets.newHashSet(Object.class.getMethods());

	private final static ConcurrentMap<MethodKey, Method> methodsCache = new ConcurrentHashMap<>();

	/**
	 * Create an instance of {@link Predicate} for {@link Method} to exclude the specified declared class
	 *
	 * @param declaredClass the declared class to exclude
	 * @return non-null
	 */
	public static Predicate<Method> excludedDeclaredClass(Class<?> declaredClass) {
		return method -> !Objects.equals(declaredClass, method.getDeclaringClass());
	}

	/**
	 * Get all {@link Method methods} of the declared class
	 *
	 * @param declaringClass        the declared class
	 * @param includeInheritedTypes include the inherited types, e,g. super classes or interfaces
	 * @param publicOnly            only public method
	 * @param methodsToFilter       (optional) the methods to be filtered
	 * @return non-null read-only {@link List}
	 */
	public static List<Method> getMethods(Class<?> declaringClass, boolean includeInheritedTypes, boolean publicOnly,
										  Predicate<? super Method>... methodsToFilter) {

		if (declaringClass == null || declaringClass.isPrimitive()) {
			return emptyList();
		}

		// All declared classes
		List<Class<?>> declaredClasses = new LinkedList<>();
		// Add the top declaring class
		declaredClasses.add(declaringClass);
		// If the super classes are resolved, all them into declaredClasses
		if (includeInheritedTypes) {
			declaredClasses.addAll(ClassUtils.getAllInheritedTypes(declaringClass));
		}

		// All methods
		List<Method> allMethods = new LinkedList<>();

		for (Class<?> classToSearch : declaredClasses) {
			Method[] methods = publicOnly ? classToSearch.getMethods() : classToSearch.getDeclaredMethods();
			// Add the declared methods or public methods
			for (Method method : methods) {
				allMethods.add(method);
			}
		}

		return unmodifiableList(Streams.filterAll(allMethods, methodsToFilter));
	}

	/**
	 * Get all declared {@link Method methods} of the declared class, excluding the inherited methods
	 *
	 * @param declaringClass  the declared class
	 * @param methodsToFilter (optional) the methods to be filtered
	 * @return non-null read-only {@link List}
	 * @see #getMethods(Class, boolean, boolean, Predicate[])
	 */
	public static List<Method> getDeclaredMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
		return getMethods(declaringClass, false, false, methodsToFilter);
	}

	/**
	 * Get all public {@link Method methods} of the declared class, including the inherited methods.
	 *
	 * @param declaringClass  the declared class
	 * @param methodsToFilter (optional) the methods to be filtered
	 * @return non-null read-only {@link List}
	 * @see #getMethods(Class, boolean, boolean, Predicate[])
	 */
	public static List<Method> getMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
		return getMethods(declaringClass, false, true, methodsToFilter);
	}

	/**
	 * Get all declared {@link Method methods} of the declared class, including the inherited methods.
	 *
	 * @param declaringClass  the declared class
	 * @param methodsToFilter (optional) the methods to be filtered
	 * @return non-null read-only {@link List}
	 * @see #getMethods(Class, boolean, boolean, Predicate[])
	 */
	public static List<Method> getAllDeclaredMethods(Class<?> declaringClass, Predicate<? super Method>... methodsToFilter) {
		return getMethods(declaringClass, true, false, methodsToFilter);
	}

	/**
	 * Get all public {@link Method methods} of the declared class, including the inherited methods.
	 *
	 * @param declaringClass  the declared class
	 * @param methodsToFilter (optional) the methods to be filtered
	 * @return non-null read-only {@link List}
	 * @see #getMethods(Class, boolean, boolean, Predicate[])
	 */
	public static List<Method> getAllMethods(Class<?> declaringClass, Predicate<Method>... methodsToFilter) {
		return getMethods(declaringClass, true, true, methodsToFilter);
	}

	/**
	 * Find the {@link Method} by the the specified type(including inherited types) and method name without the
	 * parameter type.
	 *
	 * @param type       the target type
	 * @param methodName the specified method name
	 * @return if not found, return <code>null</code>
	 */
	public static Method findMethod(Class type, String methodName) {
		return findMethod(type, methodName, EMPTY_CLASS_ARRAY);
	}

	/**
	 * Find the {@link Method} by the the specified type(including inherited types) and method name and parameter types
	 *
	 * @param type           the target type
	 * @param methodName     the method name
	 * @param parameterTypes the parameter types
	 * @return if not found, return <code>null</code>
	 */
	public static Method findMethod(Class type, String methodName, Class<?>... parameterTypes) {
		MethodKey key = MethodKey.buildKey(type, methodName, parameterTypes);
		return methodsCache.computeIfAbsent(key, MethodUtils::findMethod);
	}

	static Method findMethod(MethodKey key) {
		Class<?> declaredClass = key.declaredClass;
		String methodName = key.methodName;
		Class<?>[] parameterTypes = key.parameterTypes;
		return findDeclaredMethod(declaredClass, methodName, parameterTypes);
	}

	/**
	 * <p>findDeclaredMethod.</p>
	 *
	 * @param declaredClass  a {@link Class} object
	 * @param methodName     a {@link String} object
	 * @param parameterTypes a {@link Class} object
	 * @return a {@link Method} object
	 */
	public static Method findDeclaredMethod(Class<?> declaredClass, String methodName, Class<?>... parameterTypes) {
		Method method = getDeclaredMethod(declaredClass, methodName, parameterTypes);
		if (method == null) {
			Set<Class<?>> inheritedTypes = ClassUtils.getAllInheritedTypes(declaredClass);
			for (Class<?> inheritedType : inheritedTypes) {
				method = getDeclaredMethod(inheritedType, methodName, parameterTypes);
				if (method != null) {
					break;
				}
			}
		}
		return method;
	}

	/**
	 * <p>getDeclaredMethod.</p>
	 *
	 * @param declaredClass  a {@link Class} object
	 * @param methodName     a {@link String} object
	 * @param parameterTypes a {@link Class} object
	 * @return a {@link Method} object
	 */
	public static Method getDeclaredMethod(Class<?> declaredClass, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = declaredClass.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
		}
		return method;
	}

	/**
	 * Invoke the target objects' method
	 *
	 * @param object     the target object
	 * @param methodName the method name
	 * @param parameters the method parameters
	 * @param <T>        the return type
	 * @return the target method's execution result
	 */
	public static <T> T invokeMethod(Object object, String methodName, Object... parameters) {
		Class type = object.getClass();
		return invokeMethod(object, type, methodName, parameters);
	}

	/**
	 * Invoke the target classes' static method
	 *
	 * @param type       the target class
	 * @param methodName the method name
	 * @param parameters the method parameters
	 * @param <T>        the return type
	 * @return the target method's execution result
	 */
	public static <T> T invokeStaticMethod(Class<?> type, String methodName, Object... parameters) {
		return invokeMethod(null, type, methodName, parameters);
	}

	/**
	 * <p>invokeMethod.</p>
	 *
	 * @param instance   a {@link Object} object
	 * @param type       a {@link Class} object
	 * @param methodName a {@link String} object
	 * @param parameters a {@link Object} object
	 * @param <T>        a T class
	 * @return a T object
	 */
	public static <T> T invokeMethod(Object instance, Class<?> type, String methodName, Object... parameters) {
		Class[] parameterTypes = ClassUtils.getTypes(parameters);
		Method method = findMethod(type, methodName, parameterTypes);

		if (method == null) {
			throw new IllegalStateException(String.format("cannot find method %s,class: %s", methodName, type.getName()));
		}

		return invokeMethod(instance, method, parameters);
	}

	/**
	 * <p>invokeMethod.</p>
	 *
	 * @param instance   a {@link Object} object
	 * @param method     a {@link Method} object
	 * @param parameters a {@link Object} object
	 * @param <T>        a T class
	 * @return a T object
	 */
	public static <T> T invokeMethod(Object instance, Method method, Object... parameters) {
		return AccessibleObjectUtils.execute(method, () -> (T) method.invoke(instance, parameters));
	}

	/**
	 * Tests whether one method, as a member of a given type,
	 * overrides another method.
	 *
	 * @param overrider  the first method, possible overrider
	 * @param overridden the second method, possibly being overridden
	 * @return {@code true} if and only if the first method overrides
	 * the second
	 * @see Elements#overrides(ExecutableElement, ExecutableElement, TypeElement)
	 */
	public static boolean overrides(Method overrider, Method overridden) {

		if (overrider == null || overridden == null) {
			return false;
		}

		// equality comparison: If two methods are same
		if (Objects.equals(overrider, overridden)) {
			return false;
		}

		// Modifiers comparison: Any method must be non-static method
		if (MemberUtils.isStatic(overrider) || MemberUtils.isStatic(overridden)) { //
			return false;
		}

		// Modifiers comparison: the accessibility of any method must not be private
		if (MemberUtils.isPrivate(overrider) || MemberUtils.isPrivate(overridden)) {
			return false;
		}

		// Inheritance comparison: The declaring class of overrider must be inherit from the overridden's
		if (!overridden.getDeclaringClass().isAssignableFrom(overrider.getDeclaringClass())) {
			return false;
		}

		// Method comparison: must not be "default" method
		if (overrider.isDefault()) {
			return false;
		}

		// Method comparison: The method name must be equal
		if (!Objects.equals(overrider.getName(), overridden.getName())) {
			return false;
		}

		// Method comparison: The count of method parameters must be equal
		if (!Objects.equals(overrider.getParameterCount(), overridden.getParameterCount())) {
			return false;
		}

		// Method comparison: Any parameter type of overrider must equal the overridden's
		for (int i = 0; i < overrider.getParameterCount(); i++) {
			if (!Objects.equals(overridden.getParameterTypes()[i], overrider.getParameterTypes()[i])) {
				return false;
			}
		}

		// Method comparison: The return type of overrider must be inherit from the overridden's
		if (!overridden.getReturnType().isAssignableFrom(overrider.getReturnType())) {
			return false;
		}

		// Throwable comparison: "throws" Throwable list will be ignored, trust the compiler verify

		return true;
	}

	/**
	 * Find the nearest overridden {@link Method method} from the inherited class
	 *
	 * @param overrider the overrider {@link Method method}
	 * @return if found, the overrider <code>method</code>, or <code>null</code>
	 */
	public static Method findNearestOverriddenMethod(Method overrider) {
		Class<?> declaringClass = overrider.getDeclaringClass();
		Method overriddenMethod = null;
		for (Class<?> inheritedType : ClassUtils.getAllInheritedTypes(declaringClass)) {
			overriddenMethod = findOverriddenMethod(overrider, inheritedType);
			if (overriddenMethod != null) {
				break;
			}
		}
		return overriddenMethod;
	}

	/**
	 * Find the overridden {@link Method method} from the declaring class
	 *
	 * @param overrider      the overrider {@link Method method}
	 * @param declaringClass the class that is declaring the overridden {@link Method method}
	 * @return if found, the overrider <code>method</code>, or <code>null</code>
	 */
	public static Method findOverriddenMethod(Method overrider, Class<?> declaringClass) {
		List<Method> matchedMethods = getAllMethods(declaringClass, method -> overrides(overrider, method));
		return matchedMethods.isEmpty() ? null : matchedMethods.get(0);
	}

	/**
	 * Get the signature of {@link Method the specified method}
	 *
	 * @param method {@link Method the specified method}
	 * @return non-null
	 */
	public static String getSignature(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();
		Class<?>[] parameterTypes = method.getParameterTypes();
		int parameterCount = parameterTypes.length;
		String[] parameterTypeNames = new String[parameterCount];
		String methodName = method.getName();
		String declaringClassName = ClassUtils.getTypeName(declaringClass);
		int size = declaringClassName.length() + 1 // '#'
				   + methodName.length() + 1  // '('
				   + (parameterCount == 0 ? 0 : parameterCount - 1) // (parameterCount - 1) * ','
				   + 1  // ')'
			;

		for (int i = 0; i < parameterCount; i++) {
			Class<?> parameterType = parameterTypes[i];
			String parameterTypeName = ClassUtils.getTypeName(parameterType);
			parameterTypeNames[i] = parameterTypeName;
			size += parameterTypeName.length();
		}

		StringBuilder signatureBuilder = new StringBuilder(size);

		signatureBuilder.append(declaringClassName).append(SHARP_CHAR).append(methodName).append(LEFT_PARENTHESIS_CHAR);

		for (int i = 0; i < parameterCount; i++) {
			String parameterTypeName = parameterTypeNames[i];
			signatureBuilder.append(parameterTypeName);
			if (i < parameterCount - 1) {
				signatureBuilder.append(COMMA_CHAR);
			}
			parameterTypeNames[i] = null;
		}

		signatureBuilder.append(RIGHT_PARENTHESIS_CHAR);

		return signatureBuilder.toString();
	}

	/**
	 * <p>isObjectMethod.</p>
	 *
	 * @param method a {@link Method} object
	 * @return a boolean
	 */
	public static boolean isObjectMethod(Method method) {
		if (method != null) {
			return Objects.equals(Object.class, method.getDeclaringClass());
		}
		return false;
	}

	static class MethodKey {

		private final Class<?> declaredClass;

		private final String methodName;

		private final Class<?>[] parameterTypes;

		MethodKey(Class<?> declaredClass, String methodName, Class<?>[] parameterTypes) {
			this.declaredClass = declaredClass;
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
		}

		public static MethodKey buildKey(Class<?> declaredClass, String methodName, Class<?>[] parameterTypes) {
			return new MethodKey(declaredClass, methodName, parameterTypes);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MethodKey methodKey = (MethodKey) o;

			if (!Objects.equals(declaredClass, methodKey.declaredClass))
				return false;
			if (!Objects.equals(methodName, methodKey.methodName))
				return false;
			// Probably incorrect - comparing Object[] arrays with Arrays.equals
			return Arrays.equals(parameterTypes, methodKey.parameterTypes);
		}

		@Override
		public int hashCode() {
			int result = declaredClass != null ? declaredClass.hashCode() : 0;
			result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
			result = 31 * result + Arrays.hashCode(parameterTypes);
			return result;
		}
	}
}
