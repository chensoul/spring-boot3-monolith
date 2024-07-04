package com.chensoul.sharedlib.exception;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Accessors(chain = true)
public class RestResponse<T> extends ErrorResponse implements Serializable {
    private static final long serialVersionUID = 6551531108468957025L;

    @Getter
    private T data;

    public RestResponse(int status, int code, final T data) {
        super(status, code, null);
        this.data = data;
    }

    public RestResponse(int status, int code, String message) {
        super(status, code, message);
    }

    public RestResponse(int status, int code, String message, final T data) {
        super(status, code, message);
        this.data = data;
    }

    /**
     * <p>ok.</p>
     *
     * @param <T> a T class
     * @return a {@link RestResponse} object
     */
    public static <T> RestResponse<T> ok() {
        return new RestResponse<T>(200, 0, "OK");
    }

    /**
     * <p>ok.</p>
     *
     * @param data a T object
     * @param <T>  a T class
     * @return a {@link RestResponse} object
     */
    public static <T> RestResponse<T> ok(final T data) {
        return new RestResponse<T>(200, 0, data);
    }

    /**
     * <p>error.</p>
     *
     * @param <T> a T class
     * @return a {@link RestResponse} object
     */
    public static <T> RestResponse<T> error() {
        return error(ResultCode.INTERNAL_ERROR.getName());
    }

    /**
     * <p>error.</p>
     *
     * @param message a {@link String} object
     * @param <T>     a T class
     * @return a {@link RestResponse} object
     */
    public static <T> RestResponse<T> error(final String message) {
        return error(500, ResultCode.INTERNAL_ERROR.getCode(), message);
    }

    /**
     * <p>error.</p>
     *
     * @param code    a int
     * @param message a {@link String} object
     * @param <T>     a T class
     * @return a {@link RestResponse} object
     */
    public static <T> RestResponse<T> error(int status, int code, final String message) {
        return new RestResponse<T>(status, code, message);
    }

    public static <T> RestResponse<T> error(int code, final String message) {
        return error(500, code, message);
    }

    public static <T> RestResponse<T> error(int code, final String message, T data) {
        return new RestResponse<T>(500, code, message, data);
    }

    /**
     * <p>toMap.</p>
     *
     * @return a {@link Map} object
     */
    public Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("code", this.getCode());
        map.put("status", this.getStatus());
        map.put("message", this.getMessage());
        map.put("data", this.getData());
        map.put("timestamp", this.getTimestamp());

        return map;
    }

}
