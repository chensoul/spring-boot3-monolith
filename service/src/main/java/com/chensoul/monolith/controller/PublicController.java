package com.chensoul.monolith.controller;

import com.chensoul.monolith.service.WebhookSiteService;
import static com.chensoul.sharedlib.Constants.PUBLIC;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Public Controller
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@RestController
@RequestMapping(PublicController.BASE_URL)
@RequiredArgsConstructor
public class PublicController {
	public static final String BASE_URL = PUBLIC;

	private final WebhookSiteService webhookSiteService;

	/**
	 * helloWorld
	 *
	 * @return {@link String }
	 */
	@GetMapping("/hello-world")
	@ResponseStatus(HttpStatus.OK)
	public String helloWorld() {
		return "Hello world";
	}

	/**
	 * 调用外部api
	 *
	 * @return {@link Mono }<{@link String }>
	 */
	@GetMapping("/call-external-api")
	@ResponseStatus(HttpStatus.OK)
	public Mono<String> callExternalAPI() {
		return this.webhookSiteService.post(Map.of("test", LocalDateTime.now().toString()));
	}

	/**
	 * 使用断路器调用外部 api
	 *
	 * @return {@link Mono }<{@link String }>
	 */
	@GetMapping("/call-external-api-with-cb")
	@ResponseStatus(HttpStatus.OK)
	public Mono<String> callExternalAPIWithCircuitBreaker() {
		return this.webhookSiteService.postWithCircuitBreaker(Map.of("test", LocalDateTime.now().toString()));
	}
}
