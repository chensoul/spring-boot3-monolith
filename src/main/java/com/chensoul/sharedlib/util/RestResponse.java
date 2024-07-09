/*
 * The MIT License
 *
 *  Copyright (c) 2021, wesine.com.cn
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.chensoul.sharedlib.util;

import com.chensoul.sharedlib.errorhandler.ResultCode;
import com.chensoul.sharedlib.webmvc.SpringContextHolder;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 统一封装 Restful 接口返回信息
 * <p>
 *
 * @author zhijun.chen
 * @since 0.0.1
 */
@Data
public class RestResponse<T> {
	private static MessageSource messageSource;

	static {
		messageSource = SpringContextHolder.getBean(MessageSource.class);
	}

	private Integer code;

	private String message;

	/**
	 * 调试使用字段，不保存业务信息
	 */
	private Object error;

	/**
	 * 保存业务信息字段
	 */
	private T data;

	public RestResponse() {
	}

	public RestResponse(Integer code) {
		this(code, null, null, null);
	}

	public RestResponse(Integer code, String message) {
		this(code, message, null, null);
	}

	private RestResponse(Integer code, String message, T data) {
		this(code, message, data, null);
	}

	private RestResponse(Integer code, String message, T data, Object error) {
		this.code = code;
		if (StringUtils.isNotBlank(message)) {
			this.message = messageSource.getMessage(message, null, message, LocaleContextHolder.getLocale());
		}
		this.data = data;
		this.error = error;
	}

	public static RestResponse<Void> ok() {
		return ok(null);
	}

	public static <T> RestResponse<T> ok(T body) {
		return build(ResultCode.SUCCESS, body);
	}

	public static <T> RestResponse<T> error(String message) {
		return error(ResultCode.INTERNAL_ERROR, message, null);
	}

	public static <T> RestResponse<T> error(ResultCode resultCode) {
		return error(resultCode, resultCode.getName(), null);
	}

	public static <T> RestResponse<T> error(ResultCode resultCode, String message) {
		return build(resultCode.getCode(), message, null, null);
	}

	public static <T> RestResponse<T> error(ResultCode resultCode, Object error) {
		return build(resultCode.getCode(), resultCode.getName(), null, error);
	}

	public static <T> RestResponse<T> error(ResultCode resultCode, String message, Object error) {
		return build(resultCode.getCode(), StringUtils.isNoneBlank(message) ? message : resultCode.getName(), null, error);
	}

	public static <T> RestResponse<T> error(Integer code, String message, Object error) {
		return build(code, message, null, error);
	}

	public static <T> RestResponse<T> error(Integer code, String message, T body, Object error) {
		return build(code, message, body, error);
	}

	private static <T> RestResponse<T> build(ResultCode resultCode, T body) {
		return build(resultCode.getCode(), resultCode.getName(), body, null);
	}

	/**
	 * 以上所有构建均调用此底层方法
	 *
	 * @param stateCode 状态值
	 * @param message   返回消息
	 * @param body      返回数据体
	 */
	private static <T> RestResponse<T> build(Integer stateCode, String message, T body, Object error) {
		return new RestResponse(stateCode, message, body, error);
	}

	/**
	 * <p>toMap.</p>
	 *
	 * @return a {@link Map} object
	 */
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("code", this.getCode());
		map.put("message", this.getMessage());
		map.put("data", this.getData());
		return map;
	}
}
