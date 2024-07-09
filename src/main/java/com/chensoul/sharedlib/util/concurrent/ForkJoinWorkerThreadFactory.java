/**
 * Copyright © 2016-2024 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chensoul.sharedlib.util.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class ForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
	private final String namePrefix;
	private final AtomicLong threadNumber = new AtomicLong(1);

	public ForkJoinWorkerThreadFactory(@NonNull String namePrefix) {
		this.namePrefix = namePrefix;
	}

	@Override
	public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
		thread.setContextClassLoader(this.getClass().getClassLoader());
		thread.setName(namePrefix + "-" + thread.getPoolIndex() + "-" + threadNumber.getAndIncrement());
		return thread;
	}
}
