package com.chensoul.sharedlib.util.concurrent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveObjectExecutorServiceFactory {
	private static final Logger log = LoggerFactory.getLogger(ActiveObjectExecutorServiceFactory.class);
	private static final Map<String, ActiveObjectExecutorServiceFactory> INSTANCES = new ConcurrentHashMap<>();
	private static final int THREAD_POOL_SIZE = 5;

	private BlockingQueue<Runnable> requests;
	private String name;
	private ExecutorService executorService;
	private CompletableFuture<Void> processingFuture;
	private volatile boolean isAcceptingRequests = true;

	private ActiveObjectExecutorServiceFactory(String name, int queueSize, Long sleepMillis) {
		this.name = name;
		this.requests = new LinkedBlockingQueue<>(queueSize);
		this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new ThreadFactory() {
			private AtomicInteger count = new AtomicInteger(0);

			public Thread newThread(Runnable r) {
				return new Thread(r, "ActiveObject-" + name + "-" + count.getAndIncrement());
			}
		});
		processingFuture = CompletableFuture.runAsync(() -> {
			while (isAcceptingRequests) {
				try {
					executorService.execute(requests.take());

					if (sleepMillis != null) {
						Thread.sleep(sleepMillis);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					log.warn("Active Object thread interrupted, reason: {}", e.getMessage());
				} catch (Exception e) {
					log.error("Error processing request: {}", e.getMessage());
					// 发送告警信息
				}
			}
		}).exceptionally(e -> {
			log.error("Exception occurred in ActiveObject thread: {}", e.getMessage());
			return null;
		});
	}

	public static synchronized ActiveObjectExecutorServiceFactory getInstance(String name) {
		return getInstance(name, 16, null);
	}

	public static synchronized ActiveObjectExecutorServiceFactory getInstance(String name, int queueSize, Long sleepMillis) {
		ActiveObjectExecutorServiceFactory instance = INSTANCES.get(name);
		if (instance == null) {
			instance = new ActiveObjectExecutorServiceFactory(name, queueSize, sleepMillis);
			INSTANCES.put(name, instance);
		}
		return instance;
	}

	public void submit(Runnable task) {
		requests.offer(task);
	}

	public void stop() {
		isAcceptingRequests = false;
		processingFuture.cancel(true);
		executorService.shutdown();
	}

	public String getName() {
		return name;
	}
}
