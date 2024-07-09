package com.chensoul.sharedlib.errorhandler;

import com.chensoul.sharedlib.enums.Enumerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements Enumerable<ResultCode> {
	SUCCESS(0, "SUCCESS"),
	BAD_REQUEST(400, "BAD_REQUEST"),
	UNAUTHORIZED(401, "UNAUTHORIZED"),
	FORBIDDEN(403, "FORBIDDEN"),
	NOT_FOUND(404, "NOT_FOUND"),
	TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS"),
	INTERNAL_ERROR(500, "INTERNAL_ERROR"),
	;

	private int code;
	private String name;

}
