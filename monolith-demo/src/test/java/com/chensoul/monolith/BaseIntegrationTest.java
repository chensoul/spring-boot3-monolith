package com.chensoul.monolith;

import java.nio.file.Paths;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = {"profiling.pyroscope.enabled=false"})
public abstract class BaseIntegrationTest {

	@Container
	@ServiceConnection //自动更新配置
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

	@Container
	public static GenericContainer<?> redis = new GenericContainer<>("redis:7")
		.withExposedPorts(6379);

	static {
		Startables.deepStart(postgres, redis).join();
	}

	@Autowired
	public MockMvc mockMvc;

	@DynamicPropertySource
	static void applicationProperties(final DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
	}

	public static String random(final Integer... args) {
		return RandomStringUtils.randomAlphabetic(args.length == 0 ? 10 : args[0]);
	}

	public static String randomNumeric(final Integer... args) {
		return RandomStringUtils.randomNumeric(args.length == 0 ? 10 : args[0]);
	}

	private static String getResourcesDir() {
		return Paths.get("src", "test", "resources").toFile().getAbsolutePath();
	}

}
