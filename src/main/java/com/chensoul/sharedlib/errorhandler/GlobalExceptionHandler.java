package com.chensoul.sharedlib.errorhandler;

import com.chensoul.sharedlib.exception.BusinessException;
import com.chensoul.sharedlib.util.RestResponse;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import java.sql.BatchUpdateException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Error Exception Handler
 * <p>
 * TODO: I18N messages
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements /** AccessDeniedHandler, **/ErrorController {
	private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
	private final HttpStatus defaultStatus;

	public GlobalExceptionHandler(Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode, HttpStatus defaultStatus) {
		this.exceptionToStatusCode = exceptionToStatusCode;
		this.defaultStatus = defaultStatus;
	}

	// Process controller method parameter validations e.g. @RequestParam, @PathVariable etc.
	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(final @NotNull HandlerMethodValidationException ex, final @NotNull HttpHeaders headers, final @NotNull HttpStatusCode status, final @NotNull WebRequest request) {
		logException(ex, status);

		String message = null;
		for (ParameterValidationResult validation : ex.getAllValidationResults()) {
			for (MessageSourceResolvable error : validation.getResolvableErrors()) {
				message = error.getDefaultMessage();
				break;
			}
		}

		ProblemDetail problemDetail = ProblemDetail.build(ex, message);
		return ResponseEntity.status(BAD_REQUEST).body(RestResponse.error(ResultCode.BAD_REQUEST.getCode(), message, problemDetail));
	}


//	/*
//	 *  When authorizing user at controller or service layer using @PreAuthorize it throws
//	 * AccessDeniedException, and it's a developer's responsibility to catch it
//	 * */
//	@ResponseStatus(HttpStatus.FORBIDDEN)
//	@ExceptionHandler(AccessDeniedException.class)
//	public ProblemDetail handleAccessDeniedException(final Exception ex, final @NotNull HttpStatusCode status, final @NotNull WebRequest request) {
//		log.info(ex.getMessage(), ex);
//		return this.buildProblemDetail(HttpStatus.FORBIDDEN, null);
//	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public RestResponse handleJakartaConstraintViolationException(final ConstraintViolationException e, final @NonNull HttpStatus status, final @NonNull WebRequest request) {
		logException(e, status);

		String message = null;
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			message = constraintViolation.getMessage();
		}
		ProblemDetail problemDetail = ProblemDetail.build(e, message);
		return RestResponse.error(ResultCode.BAD_REQUEST.getCode(), message, problemDetail);
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({SQLIntegrityConstraintViolationException.class, DuplicateKeyException.class, BatchUpdateException.class, PersistenceException.class})
	public RestResponse handlePersistenceException(final Exception e, final @NonNull HttpStatus status, final @NonNull WebRequest request) {
		logException(e, status);

		final Throwable cause = NestedExceptionUtils.getMostSpecificCause(e);

		String message = ResultCode.INTERNAL_ERROR.getName();
		if (cause.getMessage().contains("Duplicate entry")) {
			final String[] split = cause.getMessage().split(" ");
			message = split[2] + "已存在";
		}

		ProblemDetail problemDetail = ProblemDetail.build(e, message);
		return RestResponse.error(ResultCode.BAD_REQUEST.getCode(), message, problemDetail);
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({BusinessException.class})
	public RestResponse handleBusinessException(final BusinessException ex, final @NonNull HttpStatus status, final @NonNull WebRequest request) {
		logException(ex, status);

		ProblemDetail problemDetail = ProblemDetail.build(ex);
		return RestResponse.error(ResultCode.INTERNAL_ERROR.getCode(), ex.getMessage(), problemDetail);
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({Exception.class})
	public RestResponse handleException(final Exception ex, final @NonNull HttpStatus status, final @NonNull WebRequest request) {
		logException(ex, status);

		ProblemDetail problemDetail = ProblemDetail.build(ex);
		return RestResponse.error(ResultCode.INTERNAL_ERROR.getCode(), ResultCode.INTERNAL_ERROR.getName(), problemDetail);
	}

	/**
	 * @param exception
	 * @param status
	 * @see <a href="https://github.com/jhipster/jhipster-lite/blob/main/src/main/java/tech/jhipster/lite/shared/error/infrastructure/primary/GeneratorErrorsHandler.java">GeneratorErrorsHandler.java</a>
	 */
	private void logException(Throwable exception, HttpStatusCode status) {
		if (status.is4xxClientError()) {
			log.info(exception.getMessage(), exception);
		} else {
			log.error(exception.getMessage(), exception);
		}
	}

	@Data
	@AllArgsConstructor
	public static class ProblemDetail {
		private String throwable;
		private String throwTime;
		private String message;
		private String traceId;

		public static ProblemDetail build(final Throwable e, String message) {
			return build(e, message, null);
		}

		public static ProblemDetail build(final Throwable e) {
			return build(e, e.getMessage());
		}

		public static ProblemDetail build(final Throwable e, String message, String traceId) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			return new ProblemDetail(rootCause.getClass().getName(), LocalDateTime.now().toString(), message, traceId);
		}
	}

}
