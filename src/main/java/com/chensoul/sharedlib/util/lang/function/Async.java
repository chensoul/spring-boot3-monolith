package com.chensoul.sharedlib.util.lang.function;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Async functions
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public abstract class Async {
	private Async() {

	}

	/**
	 * <p>supplyAsync.</p>
	 *
	 * @param supplier a {@link Supplier} object
	 * @param <U>      a U class
	 * @return a {@link CompletionStage} object
	 */
	public static <U> CompletionStage<U> supplyAsync(Supplier<U> supplier) {
		return SameExecutorCompletionStage.of(CompletableFuture.supplyAsync(supplier), null);
	}

	/**
	 * <p>supplyAsync.</p>
	 *
	 * @param supplier a {@link Supplier} object
	 * @param executor a {@link Executor} object
	 * @param <U>      a U class
	 * @return a {@link CompletionStage} object
	 */
	public static <U> CompletionStage<U> supplyAsync(Supplier<U> supplier, Executor executor) {
		return SameExecutorCompletionStage.of(CompletableFuture.supplyAsync(supplier, executor), executor);
	}

	/**
	 * <p>runAsync.</p>
	 *
	 * @param runnable a {@link Runnable} object
	 * @param executor a {@link Executor} object
	 * @return a {@link CompletionStage} object
	 */
	public static CompletionStage<Void> runAsync(Runnable runnable, Executor executor) {
		return SameExecutorCompletionStage.of(CompletableFuture.runAsync(runnable, executor), executor);
	}

	/**
	 * <p>runAsync.</p>
	 *
	 * @param runnable a {@link Runnable} object
	 * @return a {@link CompletionStage} object
	 */
	public static CompletionStage<Void> runAsync(Runnable runnable) {
		return SameExecutorCompletionStage.of(CompletableFuture.runAsync(runnable), null);
	}
}
