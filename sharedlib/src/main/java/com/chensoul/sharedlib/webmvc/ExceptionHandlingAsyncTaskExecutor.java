package com.chensoul.sharedlib.webmvc;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * <p>ExceptionHandlingAsyncTaskExecutor class.</p>
 */
public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor,
	InitializingBean, DisposableBean {

	static final String EXCEPTION_MESSAGE = "Caught task exception";

	private final Logger log = LoggerFactory.getLogger(ExceptionHandlingAsyncTaskExecutor.class);

	private final AsyncTaskExecutor executor;

	/**
	 * <p>Constructor for ExceptionHandlingAsyncTaskExecutor.</p>
	 *
	 * @param executor a {@link AsyncTaskExecutor} object.
	 */
	public ExceptionHandlingAsyncTaskExecutor(final AsyncTaskExecutor executor) {
		this.executor = executor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final Runnable task) {
		executor.execute(createWrappedRunnable(task));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public void execute(final Runnable task, final long startTimeout) {
		executor.execute(createWrappedRunnable(task), startTimeout);
	}

	private <T> Callable<T> createCallable(final Callable<T> task) {
		return () -> {
			try {
				return task.call();
			} catch (final Exception e) {
				handle(e);
				throw e;
			}
		};
	}

	private Runnable createWrappedRunnable(final Runnable task) {
		return () -> {
			try {
				task.run();
			} catch (final Exception e) {
				handle(e);
			}
		};
	}

	/**
	 * <p>handle.</p>
	 *
	 * @param e a {@link Exception} object.
	 */
	protected void handle(final Exception e) {
		log.error(EXCEPTION_MESSAGE, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<?> submit(final Runnable task) {
		return executor.submit(createWrappedRunnable(task));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		return executor.submit(createCallable(task));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() throws Exception {
		if (executor instanceof DisposableBean) {
			final DisposableBean bean = (DisposableBean) executor;
			bean.destroy();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (executor instanceof InitializingBean) {
			final InitializingBean bean = (InitializingBean) executor;
			bean.afterPropertiesSet();
		}
	}
}
