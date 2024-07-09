package com.chensoul.sharedlib.util.lang.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Slf4j
public class FunctionUtils {
	public static final Consumer<Throwable> SNEAKY_THROW = FunctionUtils::sneakyThrow;

	/**
	 * Constant <code>CHECKED_THROW</code>
	 */
	public static final Consumer<Throwable> CHECKED_THROW = FunctionUtils::checkedThrow;

	/**
	 * <p>checkedThrow.</p>
	 *
	 * @param t   a {@link Throwable} object
	 * @param <E> a E class
	 * @throws E if any.
	 */
	public static <E extends Throwable> void checkedThrow(Throwable t) throws E {
		if (t instanceof Error)
			throw (Error) t;

		if (t instanceof RuntimeException)
			throw (RuntimeException) t;

		if (t instanceof IOException)
			throw new UncheckedIOException((IOException) t);

		if (t instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		}

		throw new UncheckedException(t);
	}

	/**
	 * <p>sneakyThrow.</p>
	 *
	 * @param throwable a {@link Throwable} object
	 * @param <E>       a E class
	 * @throws E if any.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> void sneakyThrow(Throwable throwable) throws E {
		throw (E) throwable;
	}

	/**
	 * <p>doIf.</p>
	 *
	 * @param predicate    a {@link Predicate} object
	 * @param trueFunction a {@link CheckedFunction} object
	 * @param <T>          a T class
	 * @param <R>          a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> doIf(final Predicate<T> predicate,
											 final CheckedFunction<T, R> trueFunction) {
		return doIf(predicate, trueFunction, t -> null);
	}

	/**
	 * <p>doIf.</p>
	 *
	 * @param predicate     a {@link Predicate} object
	 * @param trueFunction  a {@link CheckedFunction} object
	 * @param falseFunction a {@link CheckedFunction} object
	 * @param <T>           a T class
	 * @param <R>           a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> doIf(final Predicate<T> predicate,
											 final CheckedFunction<T, R> trueFunction,
											 final CheckedFunction<T, R> falseFunction) {
		return t -> {
			try {
				if (predicate.test(t)) {
					return trueFunction.apply(t);
				}
				return falseFunction.apply(t);
			} catch (final Throwable e) {
				log.warn("doIf error", e);

				return CheckedFunction.unchecked(falseFunction).apply(t);
			}
		};
	}

	/**
	 * <p>doIf.</p>
	 *
	 * @param condition    a boolean
	 * @param trueFunction a {@link CheckedFunction} object
	 * @param <T>          a T class
	 * @param <R>          a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> doIf(final boolean condition,
											 final CheckedFunction<T, R> trueFunction) {
		return doIf(condition, trueFunction, t -> null);
	}

	/**
	 * <p>doIf.</p>
	 *
	 * @param condition     a boolean
	 * @param trueFunction  a {@link CheckedFunction} object
	 * @param falseFunction a {@link CheckedFunction} object
	 * @param <T>           a T class
	 * @param <R>           a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> doIf(final boolean condition,
											 final CheckedFunction<T, R> trueFunction,
											 final CheckedFunction<T, R> falseFunction) {
		return t -> {
			try {
				if (condition) {
					return trueFunction.apply(t);
				}
				return falseFunction.apply(t);
			} catch (final Throwable e) {
				log.warn("doIf error", e);
				return CheckedFunction.unchecked(falseFunction).apply(t);
			}
		};
	}


	/**
	 * Do if consumer.
	 *
	 * @param condition     the condition
	 * @param trueConsumer  a {@link Consumer} object
	 * @param falseConsumer a {@link Consumer} object
	 * @param <T>           a T class
	 * @return the consumer
	 */
	public static <T> Consumer<T> doIf(final boolean condition, final Consumer<T> trueConsumer, final Consumer<T> falseConsumer) {
		return t -> {
			if (condition) {
				trueConsumer.accept(t);
			} else {
				falseConsumer.accept(t);
			}
		};
	}

	/**
	 * Do if condition holds.
	 *
	 * @param <T>          the type parameter
	 * @param condition    the condition
	 * @param trueFunction the true function
	 * @return the consumer
	 */
	public static <T> Consumer<T> doIf(final boolean condition, final Consumer<T> trueFunction) {
		return doIf(condition, trueFunction, t -> {
		});
	}

	/**
	 * Do if function.
	 *
	 * @param <R>           the type parameter
	 * @param condition     the condition
	 * @param trueSupplier  the true function
	 * @param falseSupplier the false function
	 * @return the function
	 */
	public static <R> Supplier<R> doIf(final boolean condition, final Supplier<R> trueSupplier, final Supplier<R> falseSupplier) {
		return () -> {
			try {
				if (condition) {
					return trueSupplier.get();
				}
				return falseSupplier.get();
			} catch (final Throwable e) {
				log.warn("doIf error", e);
				return falseSupplier.get();
			}
		};
	}

	/**
	 * <p>doIf.</p>
	 *
	 * @param condition    a boolean
	 * @param trueSupplier a {@link Supplier} object
	 * @param <R>          a R class
	 * @return a {@link Supplier} object
	 */
	public static <R> Supplier<R> doIf(final boolean condition, final Supplier<R> trueSupplier) {
		return doIf(condition, trueSupplier, () -> null);
	}

	/**
	 * <p>tryApply.</p>
	 *
	 * @param function     a {@link CheckedFunction} object
	 * @param errorHandler a {@link CheckedFunction} object
	 * @param <T>          a T class
	 * @param <R>          a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> tryApply(final CheckedFunction<T, R> function, final CheckedFunction<Throwable, R> errorHandler) {
		return tryApply(function, errorHandler, null);
	}

	/**
	 * <p>tryApply.</p>
	 *
	 * @param function      a {@link CheckedFunction} object
	 * @param finalConsumer a {@link CheckedConsumer} object
	 * @param <T>           a T class
	 * @param <R>           a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> tryApply(final CheckedFunction<T, R> function, final CheckedConsumer<T> finalConsumer) {
		return tryApply(function, null, finalConsumer);
	}

	/**
	 * <p>tryApply.</p>
	 *
	 * @param function      a {@link CheckedFunction} object
	 * @param errorHandler  a {@link CheckedFunction} object
	 * @param finalConsumer a {@link CheckedConsumer} object
	 * @param <T>           a T class
	 * @param <R>           a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> tryApply(final CheckedFunction<T, R> function, final CheckedFunction<Throwable, R> errorHandler,
												 final CheckedConsumer<T> finalConsumer) {
		return t -> {
			try {
				return function.apply(t);
			} catch (final Throwable e) {
				log.warn("tryApply error", e);
				if (errorHandler != null) {
					return CheckedFunction.unchecked(errorHandler).apply(e);
				}
				FunctionUtils.checkedThrow(e);
				return null;
			} finally {
				if (finalConsumer != null) {
					CheckedConsumer.unchecked(finalConsumer).accept(t);
				}
			}
		};
	}


	/**
	 * <p>tryAccept.</p>
	 *
	 * @param consumer     a {@link CheckedConsumer} object
	 * @param errorHandler a {@link CheckedFunction} object
	 * @param <R>          a R class
	 * @return a {@link Consumer} object
	 */
	public static <R> Consumer<R> tryAccept(final CheckedConsumer<R> consumer, final CheckedFunction<Throwable, R> errorHandler) {
		return tryAccept(consumer, errorHandler, null);
	}

	/**
	 * Do and handle checked consumer.
	 *
	 * @param <R>           the type parameter
	 * @param consumer      the consumer
	 * @param errorHandler  the error handler
	 * @param finalConsumer a {@link CheckedConsumer} object
	 * @return the checked consumer
	 */
	public static <R> Consumer<R> tryAccept(final CheckedConsumer<R> consumer, final CheckedFunction<Throwable, R> errorHandler, final CheckedConsumer<R> finalConsumer) {
		return t -> {
			try {
				consumer.accept(t);
			} catch (final Throwable e) {
				log.warn("tryAccept error", e);
				if (errorHandler != null) {
					CheckedFunction.unchecked(errorHandler).apply(e);
				}
				FunctionUtils.CHECKED_THROW.accept(e);
			} finally {
				if (finalConsumer != null) {
					CheckedConsumer.unchecked(finalConsumer).accept(t);
				}
			}
		};
	}


	/**
	 * Do and handle supplier.
	 *
	 * @param <R>          the type parameter
	 * @param supplier     the function
	 * @param errorHandler the error handler
	 * @return the supplier
	 */
	public static <R> Supplier<R> tryGet(final CheckedSupplier<R> supplier, final CheckedFunction<Throwable, R> errorHandler) {
		return tryGet(supplier, errorHandler, null);
	}

	/**
	 * <p>tryGet.</p>
	 *
	 * @param supplier      a {@link CheckedSupplier} object
	 * @param errorHandler  a {@link CheckedFunction} object
	 * @param finalConsumer a {@link CheckedConsumer} object
	 * @param <R>           a R class
	 * @return a {@link Supplier} object
	 */
	public static <R> Supplier<R> tryGet(final CheckedSupplier<R> supplier, final CheckedFunction<Throwable, R> errorHandler, final CheckedConsumer<R> finalConsumer) {
		return () -> {
			try {
				return supplier.get();
			} catch (final Throwable e) {
				log.warn("tryGet error", e);
				if (errorHandler != null) {
					return CheckedFunction.unchecked(errorHandler).apply(e);
				}
				FunctionUtils.CHECKED_THROW.accept(e);
				return null;
			} finally {
				if (finalConsumer != null) {
					CheckedConsumer.unchecked(finalConsumer).accept(null);
				}
			}
		};
	}

	/**
	 * Throw if value is blank.
	 *
	 * @param value the value
	 * @return the value
	 * the throwable
	 * @throws Throwable if any.
	 */
	public static String throwIfBlank(final String value) throws Throwable {
		throwIf(StringUtils.isBlank(value), () -> new IllegalArgumentException("Value cannot be empty or blank"));
		return value;
	}

	/**
	 * Throw if null.
	 *
	 * @param <T>     the type parameter
	 * @param value   the value
	 * @param handler the handler
	 * @return the t
	 * the throwable
	 * @throws Throwable if any.
	 */
	public static <T> T throwIfNull(final T value, final CheckedSupplier<Throwable> handler) throws Throwable {
		throwIf(value == null, handler);
		return value;
	}

	/**
	 * Throw if.
	 *
	 * @param condition the condition
	 * @param throwable the throwable
	 *                  the throwable
	 * @throws Throwable if any.
	 */
	public static void throwIf(final boolean condition,
							   final CheckedSupplier<? extends Throwable> throwable) throws Throwable {
		if (condition) {
			throw throwable.get();
		}
	}
}
