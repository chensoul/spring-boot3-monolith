package com.chensoul.sharedlib.util.lang.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer}-like interface which allows throwing Error.
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
	/**
	 * <p>sneaky.</p>
	 *
	 * @param consumer a {@link CheckedConsumer} object
	 * @param <T>      a T class
	 * @return a {@link Consumer} object
	 */
	static <T> Consumer<T> sneaky(CheckedConsumer<T> consumer) {
		return unchecked(consumer, FunctionUtils.SNEAKY_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param consumer a {@link CheckedConsumer} object
	 * @param <T>      a T class
	 * @return a {@link Consumer} object
	 */
	static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
		return unchecked(consumer, FunctionUtils.CHECKED_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param consumer a {@link CheckedConsumer} object
	 * @param handler  a {@link Consumer} object
	 * @param <T>      a T class
	 * @return a {@link Consumer} object
	 */
	static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer, Consumer<Throwable> handler) {
		return t -> {
			try {
				consumer.accept(t);
			} catch (Throwable e) {
				handler.accept(e);
				throw new IllegalStateException("Exception handler must throw a RuntimeException", e);
			}
		};
	}

	/**
	 * <p>accept.</p>
	 *
	 * @param t a T object
	 * @throws Throwable if any.
	 */
	void accept(T t) throws Throwable;

	/**
	 * <p>andThen.</p>
	 *
	 * @param after a {@link CheckedConsumer} object
	 * @return a {@link CheckedConsumer} object
	 */
	default CheckedConsumer<T> andThen(CheckedConsumer<? super T> after) {
		Objects.requireNonNull(after, "after is null");
		return t -> {
			accept(t);
			after.accept(t);
		};
	}
}
