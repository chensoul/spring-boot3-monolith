package com.chensoul.sharedlib.util.lang.function;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
	/**
	 * <p>sneaky.</p>
	 *
	 * @param supplier a {@link CheckedSupplier} object
	 * @param <T>      a T class
	 * @return a {@link Supplier} object
	 */
	static <T> Supplier<T> sneaky(CheckedSupplier<T> supplier) {
		return unchecked(supplier, FunctionUtils.SNEAKY_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param supplier a {@link CheckedSupplier} object
	 * @param <T>      a T class
	 * @return a {@link Supplier} object
	 */
	static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier) {
		return unchecked(supplier, FunctionUtils.CHECKED_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param supplier a {@link CheckedSupplier} object
	 * @param handler  a {@link Consumer} object
	 * @param <T>      a T class
	 * @return a {@link Supplier} object
	 */
	static <T> Supplier<T> unchecked(CheckedSupplier<T> supplier, Consumer<Throwable> handler) {
		return () -> {
			try {
				return supplier.get();
			} catch (Throwable e) {
				handler.accept(e);
				throw new IllegalStateException("Exception handler must throw a RuntimeException", e);
			}
		};
	}

	T get() throws Throwable;

	/**
	 * <p>andThen.</p>
	 *
	 * @param after a {@link CheckedFunction} object
	 * @param <V>   a V class
	 * @return a {@link CheckedSupplier} object
	 */
	default <V> CheckedSupplier<V> andThen(CheckedFunction<? super T, ? extends V> after) {
		Objects.requireNonNull(after, "after is null");
		return () -> after.apply(get());
	}

}
