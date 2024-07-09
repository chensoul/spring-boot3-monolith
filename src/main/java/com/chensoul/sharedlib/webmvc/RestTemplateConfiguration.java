package com.chensoul.sharedlib.webmvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @see RestTemplateAutoConfiguration
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class RestTemplateConfiguration {
	@Autowired
	CloseableHttpClient httpClient;

	@Bean
	public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
		return configurer.configure(new RestTemplateBuilder())
			.setConnectTimeout(Duration.ofSeconds(5))
			.setReadTimeout(Duration.ofSeconds(2));
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory()));
		restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));
		return restTemplate;
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		return clientHttpRequestFactory;
	}

//	@Bean
//	public TaskScheduler taskScheduler() {
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setThreadNamePrefix("scheduler-");
//		scheduler.setPoolSize(4);
//		return scheduler;
//	}

	/**
	 * @see <a href="https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-httpclient-java-config/">Configuring HttpClient with Spring RestTemplate</a>
	 * <p>
	 *
	 * <ul>
	 *     <ol>1. Supports both HTTP and HTTPS</ol>
	 *     <ol>2. Uses a connection pool to re-use connections and save overhead of creating connections.</ol>
	 *     <ol>3. Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)</ol>
	 *     <ol>4. Starts an idle connection monitor to continuously clean up stale connections.</ol>
	 * </ul>
	 */
	@Configuration
	@EnableScheduling
	public static class HttpClientConfig {
		// Determines the timeout in milliseconds until a connection is established.
		private static final int CONNECT_TIMEOUT = 30000;
		// The timeout when requesting a connection from the connection manager.
		private static final int REQUEST_TIMEOUT = 30000;
		// The timeout for waiting for data
		private static final int SOCKET_TIMEOUT = 60000;
		private static final int MAX_TOTAL_CONNECTIONS = 50;
		private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
		private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

		@Bean
		public PoolingHttpClientConnectionManager poolingConnectionManager() {
			SSLContextBuilder builder = new SSLContextBuilder();
			try {
				builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			} catch (NoSuchAlgorithmException | KeyStoreException e) {
				log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
			}
			SSLConnectionSocketFactory sslsf = null;
			try {
				sslsf = new SSLConnectionSocketFactory(builder.build());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
			}
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("https", sslsf)
				.register("http", new PlainConnectionSocketFactory())
				.build();
			PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
			return poolingConnectionManager;
		}

		@Bean
		public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
			return new ConnectionKeepAliveStrategy() {
				@Override
				public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
					final Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);
					while (it.hasNext()) {
						HeaderElement he = it.next();
						String param = he.getName();
						String value = he.getValue();
						if (value != null && param.equalsIgnoreCase("timeout")) {
							return TimeValue.of(Long.parseLong(value) * 1000, TimeUnit.MICROSECONDS);

						}
					}
					return TimeValue.of(DEFAULT_KEEP_ALIVE_TIME_MILLIS, TimeUnit.MICROSECONDS);
				}
			};
		}

		@Bean
		public CloseableHttpClient httpClient() {
			RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.of(REQUEST_TIMEOUT, TimeUnit.MICROSECONDS))
				.setConnectTimeout(Timeout.of(CONNECT_TIMEOUT, TimeUnit.MICROSECONDS))
				.build();
			return HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolingConnectionManager())
				.setKeepAliveStrategy(connectionKeepAliveStrategy())
				.build();
		}

		@Bean
		public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
			return new Runnable() {
				@Override
				@Scheduled(fixedDelay = 10000)
				public void run() {
					try {
						if (connectionManager != null) {
							log.debug("run IdleConnectionMonitor - Closing expired and idle connections...");
							connectionManager.close();
							connectionManager.closeIdle(TimeValue.of(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS));
						} else {
							log.info("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
						}
					} catch (Exception e) {
						log.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
					}
				}
			};
		}
	}

	@Slf4j
	public static class LoggingInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			logRequest(request, body);
			ClientHttpResponse response = execution.execute(request, body);
			logResponse(response);
			//Add optional additional headers
			response.getHeaders().add("headerName", "VALUE");
			return response;
		}

		private void logRequest(HttpRequest request, byte[] body) throws IOException {
			if (log.isDebugEnabled()) {
				log.debug("===========================request begin================================================");
				log.debug("URI         : {}", request.getURI());
				log.debug("Method      : {}", request.getMethod());
				log.debug("Headers     : {}", request.getHeaders());
				log.debug("Request body: {}", new String(body, "UTF-8"));
				log.debug("==========================request end================================================");
			}
		}

		private void logResponse(ClientHttpResponse response) throws IOException {
			if (log.isDebugEnabled()) {
				log.debug("============================response begin==========================================");
				log.debug("Status code  : {}", response.getStatusCode());
				log.debug("Status text  : {}", response.getStatusText());
				log.debug("Headers      : {}", response.getHeaders());
				log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
				log.debug("=======================response end=================================================");
			}
		}
	}

}
