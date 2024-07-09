package com.chensoul.sharedlib.util.lang.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A {@link BiFunction} that allows for checked exceptions.
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@FunctionalInterface
public interface CheckedBiFunction<T, U, R> {
	/**
	 * <p>sneaky.</p>
	 *
	 * @param consumer a {@link CheckedBiFunction} object
	 * @param <T>      a T class
	 * @param <U>      a U class
	 * @param <R>      a R class
	 * @return a {@link BiFunction} object
	 */
	static <T, U, R> BiFunction<T, U, R> sneaky(CheckedBiFunction<T, U, R> consumer) {
		return unchecked(consumer, FunctionUtils.SNEAKY_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param consumer a {@link CheckedBiFunction} object
	 * @param <T>      a T class
	 * @param <U>      a U class
	 * @param <R>      a R class
	 * @return a {@link BiFunction} object
	 */
	static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> consumer) {
		return unchecked(consumer, FunctionUtils.CHECKED_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param consumer a {@link CheckedBiFunction} object
	 * @param handler  a {@link Consumer} object
	 * @param <T>      a T class
	 * @param <U>      a U class
	 * @param <R>      a R class
	 * @return a {@link BiFunction} object
	 */
	static <T, U, R> BiFunction<T, U, R> unchecked(CheckedBiFunction<T, U, R> consumer, Consumer<Throwable> handler) {
		return (t, u) -> {
			try {
				return consumer.apply(t, u);
			} catch (Throwable e) {
				handler.accept(e);
				throw new IllegalStateException("Exception handler must throw a RuntimeException", e);
			}
		};
	}

	/**
	 * <p>apply.</p>
	 *
	 * @param t a T object
	 * @param u a U object
	 * @return a R object
	 * @throws Throwable if any.
	 */
	R apply(T t, U u) throws Throwable;
}
