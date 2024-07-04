package com.chensoul.sharedlib.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class DefaultRateLimit extends BaseRateLimit {

	@Value("${rate-limit.default.name:DEFAULT}")
	private String name;

	@Value("${rate-limit.default.max-requests:50}")
	private int maxRequests;

	@Value("${rate-limit.default.refill-in-seconds:1}")
	private int refillInSeconds;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Bandwidth getLimit() {
		return Bandwidth.classic(
			this.maxRequests,
			Refill.intervally(this.maxRequests, Duration.ofSeconds(this.refillInSeconds)));
	}
}
