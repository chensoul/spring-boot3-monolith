package com.chensoul.sharedlib.ratelimit;

import io.github.bucket4j.Bandwidth;

public abstract class BaseRateLimit {
	public abstract String getName();

	public abstract Bandwidth getLimit();
}
