package com.chensoul.sharedlib.tenant;

import static com.chensoul.sharedlib.util.StringPool.TENANT_ID_HEADER;
import static com.chensoul.sharedlib.util.StringPool.UNKNOWN;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "application", name = "multitenancy.enabled", havingValue = "true")
public class TenantRoutingFilter extends OncePerRequestFilter {
	private List<String> includePatterns;
	private List<String> excludePatterns;

	public TenantRoutingFilter() {
		log.info("Adding TenantRoutingFilter to existing filter chain");
	}

	public static void main(String[] args) {
		List<String> includePatterns = Arrays.asList("/v1/**", "/wx/**");
		List<String> excludePatterns = Arrays.asList("/v1/captcha", "/wx/user/login");
		String requestUri = "/wx/user/login";
		System.out.println(includePatterns.stream().anyMatch(e -> new AntPathMatcher().match(e, requestUri)));
		System.out.println(excludePatterns.stream().anyMatch(e -> new AntPathMatcher().match(e, requestUri)));
	}

	@Override
	@SneakyThrows
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestUri = request.getRequestURI().replace("//", "/");
		return this.checkExclude(requestUri) || !this.checkInclude(requestUri);
	}

	public final boolean checkInclude(String requestUri) {
		return includePatterns.stream().anyMatch(e -> new AntPathMatcher().match(e, requestUri));
	}

	public final boolean checkExclude(String requestUri) {
		return excludePatterns.stream().anyMatch(e -> new AntPathMatcher().match(e, requestUri));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		final String tenantId = request.getHeader(TENANT_ID_HEADER);
		if (log.isDebugEnabled()) {
			log.debug("{}, {}", request.getRequestURI(), tenantId);
		}

		try {
			if (StringUtils.isNotBlank(tenantId)) {
				TenantContextHolder.setTenantId(tenantId);
			} else {
				TenantContextHolder.setTenantId(UNKNOWN);
			}
		} finally {
			filterChain.doFilter(request, response);
			TenantContextHolder.clear();
		}
	}
}
