package com.chensoul.sharedlib.util.reflect;

import com.chensoul.sharedlib.util.lang.function.CheckedConsumer;
import com.chensoul.sharedlib.util.lang.function.CheckedFunction;
import com.chensoul.sharedlib.util.lang.function.CheckedSupplier;
import com.chensoul.sharedlib.util.lang.function.FunctionUtils;
import java.lang.reflect.AccessibleObject;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public abstract class AccessibleObjectUtils {
	public static <A extends AccessibleObject> void execute(A accessibleObject, CheckedConsumer<A> consumer) {
		execute(accessibleObject, a -> {
			consumer.accept(a);
			return null;
		});
	}

	/**
	 * Execute an {@link AccessibleObject} instance
	 *
	 * @param accessibleObject {@link AccessibleObject} instance, {@link java.lang.reflect.Field}, {@link java.lang.reflect.Method} or {@link java.lang.reflect.Constructor}
	 * @param supplier         the supplier to execute {@link AccessibleObject} object
	 * @param <A>              a A class
	 * @param <R>              a R class
	 * @return {@link R}       the execution result
	 */
	public static <A extends AccessibleObject, R> R execute(A accessibleObject, CheckedSupplier<R> supplier) {
		return execute(accessibleObject, (CheckedFunction<A, R>) a -> supplier.get());
	}

	/**
	 * Execute an {@link AccessibleObject} instance
	 *
	 * @param accessibleObject {@link AccessibleObject} instance, {@link java.lang.reflect.Field}, {@link java.lang.reflect.Method} or {@link java.lang.reflect.Constructor}
	 * @param function         the function to execute {@link AccessibleObject} object
	 * @param <A>              a A class
	 * @param <R>              a R class
	 * @return {@link R}       the execution result
	 */
	public static <A extends AccessibleObject, R> R execute(A accessibleObject, CheckedFunction<A, R> function) {
		boolean accessible = accessibleObject.isAccessible();

		if (!accessible) {
			accessibleObject.setAccessible(true);
		}

		return FunctionUtils.tryApply(function, t -> {
			accessibleObject.setAccessible(accessible);
		}).apply(accessibleObject);
	}
}
