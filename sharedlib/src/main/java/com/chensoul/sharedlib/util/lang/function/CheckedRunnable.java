package com.chensoul.sharedlib.util.lang.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Runnable}-like interface which allows throwing Error.
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@FunctionalInterface
public interface CheckedRunnable {
	/**
	 * <p>sneaky.</p>
	 *
	 * @param runnable a {@link CheckedRunnable} object
	 * @return a {@link Runnable} object
	 */
	static Runnable sneaky(CheckedRunnable runnable) {
		return unchecked(runnable, FunctionUtils.SNEAKY_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param runnable a {@link CheckedRunnable} object
	 * @return a {@link Runnable} object
	 */
	static Runnable unchecked(CheckedRunnable runnable) {
		return unchecked(runnable, FunctionUtils.CHECKED_THROW);
	}

	/**
	 * <p>unchecked.</p>
	 *
	 * @param runnable a {@link CheckedRunnable} object
	 * @param handler  a {@link Consumer} object
	 * @return a {@link Runnable} object
	 */
	static Runnable unchecked(CheckedRunnable runnable, Consumer<Throwable> handler) {
		return () -> {
			try {
				runnable.run();
			} catch (Throwable e) {
				handler.accept(e);
				throw new IllegalStateException("Exception handler must throw a RuntimeException", e);
			}
		};
	}

	/**
	 * <p>run.</p>
	 *
	 * @throws Throwable if any.
	 */
	void run() throws Throwable;

	/**
	 * <p>andThen.</p>
	 *
	 * @param after a {@link CheckedRunnable} object
	 * @return a {@link CheckedRunnable} object
	 */
	default CheckedRunnable andThen(CheckedRunnable after) {
		Objects.requireNonNull(after, "after is null");
		return () -> {
			run();
			after.run();
		};
	}

}
