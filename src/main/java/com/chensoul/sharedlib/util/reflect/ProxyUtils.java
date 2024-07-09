package com.chensoul.sharedlib.util.reflect;

import static com.chensoul.sharedlib.util.lang.function.Predicates.and;
import static com.chensoul.sharedlib.util.reflect.ClassUtils.isPrimitive;
import static com.chensoul.sharedlib.util.reflect.MemberUtils.FINAL_METHOD_PREDICATE;
import static com.chensoul.sharedlib.util.reflect.MemberUtils.NON_PRIVATE_METHOD_PREDICATE;
import static com.chensoul.sharedlib.util.reflect.MemberUtils.NON_STATIC_METHOD_PREDICATE;
import static com.chensoul.sharedlib.util.reflect.MethodUtils.OBJECT_METHOD_PREDICATE;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isFinal;
import java.util.List;
import java.util.function.Predicate;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public abstract class ProxyUtils {

	/**
	 * <ul>
	 *     <li>class has a non-private constructor with no parameters</li>
	 *     <li>class is not declared final</li>
	 *     <li>class does not have non-static, final methods with public, protected or default visibility</li>
	 *     <li>class is not primitive type</li>
	 *     <li>class is not array type</li>
	 * </ul>
	 *
	 * @param type a {@link Class} object
	 * @return a boolean
	 */
	public static boolean isProxyable(Class<?> type) {
		if (type != null && type.getClass().isArray()) {
			return false;
		}

		if (isPrimitive(type)) {
			return false;
		}

		if (isFinal(type.getModifiers())) {
			return false;
		}

		if (!ConstructorUtils.hasNonPrivateConstructorWithoutParameters(type)) {
			return false;
		}

		Predicate<? super Method> predicate = and(NON_STATIC_METHOD_PREDICATE, FINAL_METHOD_PREDICATE,
			NON_PRIVATE_METHOD_PREDICATE, OBJECT_METHOD_PREDICATE.negate());

		List<Method> methods = MethodUtils.getAllDeclaredMethods(type, predicate);

		if (!methods.isEmpty()) {
			return false;
		}

		return true;
	}
}
