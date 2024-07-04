package com.chensoul.sharedlib.profiling;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "profiling", name = "pyroscope.enabled", havingValue = "true")
public class PyroscopeConfiguration {
	@Value("${profiling.pyroscope.url}")
	private final String pyroscopeServer;

	@PostConstruct
	public void init() {
		PyroscopeAgent.start(new Config.Builder()
			.setApplicationName("spring-boot3-monolith")
			.setProfilingEvent(EventType.ITIMER)
			.setProfilingAlloc("512k")
			// .setAllocLive(true)
			.setFormat(Format.JFR)
			.setServerAddress(pyroscopeServer).build());
	}
}
