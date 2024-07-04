package com.chensoul.sharedlib.util.lang.function;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
final class SameExecutorCompletionStage<T> implements CompletionStage<T> {
	private final CompletionStage<T> delegate;
	private final Executor defaultExecutor;

	SameExecutorCompletionStage(final CompletionStage<T> delegate, final Executor defaultExecutor) {
		this.delegate = delegate;
		this.defaultExecutor = defaultExecutor;
	}

	static final <T> SameExecutorCompletionStage<T> of(final CompletionStage<T> delegate, final Executor defaultExecutor) {
		return new SameExecutorCompletionStage<>(delegate, defaultExecutor);
	}

	@Override
	public final <U> CompletionStage<U> thenApply(final Function<? super T, ? extends U> fn) {
		return of(delegate.thenApply(fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> thenApplyAsync(final Function<? super T, ? extends U> fn) {
		if (defaultExecutor == null) {
			return of(delegate.thenApplyAsync(fn), null);
		}
		return of(delegate.thenApplyAsync(fn, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> thenApplyAsync(final Function<? super T, ? extends U> fn, final Executor executor) {
		return of(delegate.thenApplyAsync(fn, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenAccept(final Consumer<? super T> action) {
		return of(delegate.thenAccept(action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenAcceptAsync(final Consumer<? super T> action) {
		if (defaultExecutor == null) {
			return of(delegate.thenAcceptAsync(action), null);
		}
		return of(delegate.thenAcceptAsync(action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenAcceptAsync(final Consumer<? super T> action, final Executor executor) {
		return of(delegate.thenAcceptAsync(action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenRun(final Runnable action) {
		return of(delegate.thenRun(action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenRunAsync(final Runnable action) {
		if (defaultExecutor == null) {
			return of(delegate.thenRunAsync(action), null);
		}
		return of(delegate.thenRunAsync(action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> thenRunAsync(final Runnable action, final Executor executor) {
		return of(delegate.thenRunAsync(action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U, V> CompletionStage<V> thenCombine(final CompletionStage<? extends U> other,
													   final BiFunction<? super T, ? super U, ? extends V> fn) {
		return of(delegate.thenCombine(other, fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U, V> CompletionStage<V> thenCombineAsync(final CompletionStage<? extends U> other,
															final BiFunction<? super T, ? super U, ? extends V> fn) {
		if (defaultExecutor == null) {
			return of(delegate.thenCombineAsync(other, fn), null);
		}
		return of(delegate.thenCombineAsync(other, fn, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U, V> CompletionStage<V> thenCombineAsync(final CompletionStage<? extends U> other,
															final BiFunction<? super T, ? super U, ? extends V> fn, final Executor executor) {
		return of(delegate.thenCombineAsync(other, fn, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<Void> thenAcceptBoth(final CompletionStage<? extends U> other,
														  final BiConsumer<? super T, ? super U> action) {
		return of(delegate.thenAcceptBoth(other, action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<Void> thenAcceptBothAsync(final CompletionStage<? extends U> other,
															   final BiConsumer<? super T, ? super U> action) {
		if (defaultExecutor == null) {
			return of(delegate.thenAcceptBothAsync(other, action), null);
		}
		return of(delegate.thenAcceptBothAsync(other, action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<Void> thenAcceptBothAsync(final CompletionStage<? extends U> other,
															   final BiConsumer<? super T, ? super U> action, final Executor executor) {
		return of(delegate.thenAcceptBothAsync(other, action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterBoth(final CompletionStage<?> other, final Runnable action) {
		return of(delegate.runAfterBoth(other, action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterBothAsync(final CompletionStage<?> other, final Runnable action) {
		if (defaultExecutor == null) {
			return of(delegate.runAfterBothAsync(other, action), null);
		}
		return of(delegate.runAfterBothAsync(other, action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterBothAsync(final CompletionStage<?> other, final Runnable action, final Executor executor) {
		return of(delegate.runAfterBothAsync(other, action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> applyToEither(final CompletionStage<? extends T> other, final Function<? super T, U> fn) {
		return of(delegate.applyToEither(other, fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> applyToEitherAsync(final CompletionStage<? extends T> other, final Function<? super T, U> fn) {
		if (defaultExecutor == null) {
			return of(delegate.applyToEitherAsync(other, fn), null);
		}
		return of(delegate.applyToEitherAsync(other, fn, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> applyToEitherAsync(final CompletionStage<? extends T> other, final Function<? super T, U> fn,
														   final Executor executor) {
		return of(delegate.applyToEitherAsync(other, fn, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> acceptEither(final CompletionStage<? extends T> other, final Consumer<? super T> action) {
		return of(delegate.acceptEither(other, action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> acceptEitherAsync(final CompletionStage<? extends T> other, final Consumer<? super T> action) {
		if (defaultExecutor == null) {
			return of(delegate.acceptEitherAsync(other, action), null);
		}
		return of(delegate.acceptEitherAsync(other, action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> acceptEitherAsync(final CompletionStage<? extends T> other, final Consumer<? super T> action,
														 final Executor executor) {
		return of(delegate.acceptEitherAsync(other, action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterEither(final CompletionStage<?> other, final Runnable action) {
		return of(delegate.runAfterEither(other, action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterEitherAsync(final CompletionStage<?> other, final Runnable action) {
		if (defaultExecutor == null) {
			return of(delegate.runAfterEitherAsync(other, action), null);
		}
		return of(delegate.runAfterEitherAsync(other, action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<Void> runAfterEitherAsync(final CompletionStage<?> other, final Runnable action, final Executor executor) {
		return of(delegate.runAfterEitherAsync(other, action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> thenCompose(final Function<? super T, ? extends CompletionStage<U>> fn) {
		return of(delegate.thenCompose(fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> thenComposeAsync(final Function<? super T, ? extends CompletionStage<U>> fn) {
		if (defaultExecutor == null) {
			return of(delegate.thenComposeAsync(fn), null);
		}
		return of(delegate.thenComposeAsync(fn, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> thenComposeAsync(final Function<? super T, ? extends CompletionStage<U>> fn,
														 final Executor executor) {
		return of(delegate.thenComposeAsync(fn, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<T> exceptionally(final Function<Throwable, ? extends T> fn) {
		return of(delegate.exceptionally(fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<T> whenComplete(final BiConsumer<? super T, ? super Throwable> action) {
		return of(delegate.whenComplete(action), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<T> whenCompleteAsync(final BiConsumer<? super T, ? super Throwable> action) {
		if (defaultExecutor == null) {
			return of(delegate.whenCompleteAsync(action), null);
		}
		return of(delegate.whenCompleteAsync(action, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletionStage<T> whenCompleteAsync(final BiConsumer<? super T, ? super Throwable> action, final Executor executor) {
		return of(delegate.whenCompleteAsync(action, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> handle(final BiFunction<? super T, Throwable, ? extends U> fn) {
		return of(delegate.handle(fn), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> handleAsync(final BiFunction<? super T, Throwable, ? extends U> fn) {
		if (defaultExecutor == null) {
			return of(delegate.handleAsync(fn), null);
		}
		return of(delegate.handleAsync(fn, defaultExecutor), defaultExecutor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <U> CompletionStage<U> handleAsync(final BiFunction<? super T, Throwable, ? extends U> fn, final Executor executor) {
		return of(delegate.handleAsync(fn, executor), executor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CompletableFuture<T> toCompletableFuture() {
		return delegate.toCompletableFuture();
	}
}
