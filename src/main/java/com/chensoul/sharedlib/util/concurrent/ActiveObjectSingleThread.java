package com.chensoul.sharedlib.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 活动对象设计模式。使用 BlockingQueue 存储请求，使用单个线程来处理 BlockingQueue 中的请求。
 */
@Slf4j
public abstract class ActiveObjectSingleThread {
	private BlockingQueue<Runnable> requests;

	private String name;

	private Thread thread;

	private volatile boolean isAcceptingRequests = true;
	private volatile boolean isProcessingRequests = true;

	public ActiveObjectSingleThread(String name) {
		this(name, 16, null);
	}

	public ActiveObjectSingleThread(String name, int queueSize, Long sleepMillis) {
		this.name = name;
		this.requests = new LinkedBlockingQueue<>(queueSize);

		thread = new Thread(() -> {
			while (isProcessingRequests) {
				try {
					processRequest(requests.take());

					if (sleepMillis != null) {
						Thread.sleep(sleepMillis);
					}
				} catch (InterruptedException e) {
					log.warn("Active Object thread interrupted, reason: {}", e.getMessage());
				}
			}
		}, name);
		thread.start();
	}

	/**
	 * 处理请求，需要捕获业务异常，记录日志或者发送告警信息
	 *
	 * @param task
	 */
	private void processRequest(Runnable task) {
		try {
			task.run();
		} catch (Exception e) {
			log.error("Error processing request: {}", e.getMessage());
			// 发送告警信息
		}
	}

	public void shutdown() {
		isAcceptingRequests = false;
		while (!requests.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Ignore exception
			}
		}
		isProcessingRequests = false;
		thread.interrupt();
	}

	public void run(Runnable runnable) {
		if (isAcceptingRequests) {
			requests.offer(runnable);
		} else {
			throw new IllegalStateException("Active object is no longer accepting requests");
		}
	}

	public String name() {
		return this.name;
	}
}
