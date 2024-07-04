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
package com.chensoul.sharedlib.errorhandler;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.ToString;

@ToString
public class ErrorResponse {
	private final int status;

	private final int code;

	private final String message;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime timestamp;

	protected ErrorResponse(int status, final int code, final String message) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	public static ErrorResponse of(int status, final int code, final String message) {
		return new ErrorResponse(status, code, message);
	}

	public Integer getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
