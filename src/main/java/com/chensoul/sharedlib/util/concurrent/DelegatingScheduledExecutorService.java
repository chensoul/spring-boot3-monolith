package com.chensoul.sharedlib.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public class DelegatingScheduledExecutorService implements ScheduledExecutorService {
	private volatile ScheduledExecutorService delegate;

	/**
	 * <p>Constructor for DelegatingScheduledExecutorService.</p>
	 *
	 * @param delegate a {@link ScheduledExecutorService} object
	 */
	public DelegatingScheduledExecutorService(ScheduledExecutorService delegate) {
		this.delegate = delegate;
	}

	/**
	 * <p>Getter for the field <code>delegate</code>.</p>
	 *
	 * @return {@link ScheduledExecutorService}
	 */
	public ScheduledExecutorService getDelegate() {
		return delegate;
	}

	/**
	 * <p>Setter for the field <code>delegate</code>.</p>
	 *
	 * @param delegate a {@link ScheduledExecutorService} object
	 */
	public void setDelegate(ScheduledExecutorService delegate) {
		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return getDelegate().schedule(command, delay, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return getDelegate().schedule(callable, delay, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return getDelegate().scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return getDelegate().scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() {
		getDelegate().shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Runnable> shutdownNow() {
		return getDelegate().shutdownNow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isShutdown() {
		return getDelegate().isShutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTerminated() {
		return getDelegate().isTerminated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return getDelegate().awaitTermination(timeout, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return getDelegate().submit(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return getDelegate().submit(task, result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<?> submit(Runnable task) {
		return getDelegate().submit(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return getDelegate().invokeAll(tasks);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return getDelegate().invokeAll(tasks, timeout, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return getDelegate().invokeAny(tasks);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return getDelegate().invokeAny(tasks, timeout, unit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Runnable command) {
		getDelegate().execute(command);
	}
}
