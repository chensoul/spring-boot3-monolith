package com.chensoul.sharedlib.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {
	private static final String BASIC_ = "Basic ";
	private static final String AUTHORIZATION = "AUTHORIZATION";

	/**
	 * 获取request
	 */
	public static HttpServletRequest getRequest() {
		return getRequestAttributes().getRequest();
	}

	/**
	 * 获取response
	 */
	public static HttpServletResponse getResponse() {
		return getRequestAttributes().getResponse();
	}

	/**
	 * 获取session
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static ServletRequestAttributes getRequestAttributes() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		return (ServletRequestAttributes) attributes;
	}

	public static String getValueFromRequest(HttpServletRequest request, String header) {
		String token = request.getParameter(header);
		if (StringUtils.isNotBlank(token)) {
			return token;
		} else {
			token = request.getHeader(header);
		}
		return token;
	}

	public static String getValueFromRequest(ServletRequest servletRequest, String header) {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		return getValueFromRequest(httpRequest, header);
	}

	public static void renderJson(HttpServletResponse response, Object result) {
		renderJson(response, HttpServletResponse.SC_OK, result);
	}

	public static void renderJson(HttpServletResponse response, int httpStatus, Object result) {
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(httpStatus);
		try (PrintWriter out = response.getWriter()) {
			out.append(JsonUtils.toString(result));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}


	/**
	 * 是否是Ajax异步请求
	 *
	 * @param request
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (accept != null && accept.indexOf("application/json") != -1) {
			return Boolean.TRUE;
		}

		String xRequestedWith = request.getHeader("X-Requested-With");
		if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@SneakyThrows
	private static String[] getClientId(String header) {
		if (header == null || !header.startsWith(BASIC_)) {
			throw new RuntimeException("请求头中client信息为空");
		}
		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, StandardCharsets.UTF_8);

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new RuntimeException("Invalid basic authentication token");
		}
		return new String[]{token.substring(0, delim), token.substring(delim + 1)};
	}

	@SneakyThrows
	public static String[] getClientId(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION);
		return getClientId(header);
	}

	public static String createMessage(HttpServletRequest request) {
		return createMessage(request, true, true, true, true, null);
	}

	public static String createMessage(HttpServletRequest request, Predicate<String> headerPredicate) {
		return createMessage(request, true, true, true, true, headerPredicate);
	}

	/**
	 * 参考 AbstractRequestLoggingFilter
	 *
	 * @param request
	 * @param isIncludeQueryString
	 * @param isIncludeClientInfo
	 * @param isIncludePayload
	 * @param isIncludeHeaders
	 * @param headerPredicate
	 * @return
	 * @see AbstractRequestLoggingFilter
	 */
	public static String createMessage(HttpServletRequest request, Boolean isIncludeQueryString, Boolean isIncludeClientInfo, Boolean isIncludePayload, Boolean isIncludeHeaders, Predicate<String> headerPredicate) {
		StringBuilder msg = new StringBuilder();
		msg.append(request.getMethod()).append(' ');
		msg.append(request.getRequestURI());

		if (isIncludeQueryString) {
			String queryString = request.getQueryString();
			if (queryString != null) {
				msg.append('?').append(queryString);
			}
		}

		if (isIncludeClientInfo) {
			String client = request.getRemoteAddr();
			if (org.springframework.util.StringUtils.hasLength(client)) {
				msg.append(", client=").append(client);
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append(", session=").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append(", user=").append(user);
			}
		}

		if (isIncludeHeaders) {
			HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
			if (headerPredicate != null) {
				Enumeration<String> names = request.getHeaderNames();
				while (names.hasMoreElements()) {
					String header = names.nextElement();
					if (!headerPredicate.test(header)) {
						headers.set(header, "masked");
					}
				}
			}
			msg.append(", headers=").append(headers);
		}

		if (isIncludePayload) {
			String payload = getMessagePayload(request);
			if (payload != null) {
				msg.append(", payload=").append(payload);
			}
		}

		return msg.toString();
	}

	protected static String getMessagePayload(HttpServletRequest request) {
		ContentCachingRequestWrapper wrapper =
			org.springframework.web.util.WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				int length = Math.min(buf.length, 64000);
				try {
					return new String(buf, 0, length, wrapper.getCharacterEncoding());
				} catch (UnsupportedEncodingException ex) {
					return "[unknown]";
				}
			}
		}
		return null;
	}
}
