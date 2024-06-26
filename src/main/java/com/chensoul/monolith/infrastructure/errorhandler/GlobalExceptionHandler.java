package com.chensoul.monolith.infrastructure.errorhandler;

import com.chensoul.monolith.common.exception.BusinessException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.NotNull;
import static java.lang.String.format;
import java.nio.file.AccessDeniedException;
import java.sql.BatchUpdateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Error Exception Handler
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements /** AccessDeniedHandler, **/ErrorController {
	public static final String API_DEFAULT_ERROR_MESSAGE = "Something went wrong. Please try again later or enter in contact with our service.";

	// Process @Valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull final MethodArgumentNotValidException ex, @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode status, @NonNull final WebRequest request) {
		log.info(ex.getMessage(), ex);

		final List<ErrorDetail> errors = new ArrayList<>();

		for (final ObjectError err : ex.getBindingResult().getAllErrors()) {
			errors.add(ErrorDetail.builder().pointer(((FieldError) err).getField()).reason(err.getDefaultMessage()).build());
		}

		return ResponseEntity.status(BAD_REQUEST).body(this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors));
	}

	// Process controller method parameter validations e.g. @RequestParam, @PathVariable etc.
	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(final @NotNull HandlerMethodValidationException ex, final @NotNull HttpHeaders headers, final @NotNull HttpStatusCode status, final @NotNull WebRequest request) {
		log.info(ex.getMessage(), ex);

		final List<ErrorDetail> errors = new ArrayList<>();
		for (final var validation : ex.getAllValidationResults()) {
			final String parameterName = validation.getMethodParameter().getParameterName();
			validation.getResolvableErrors().forEach(error -> {
				errors.add(ErrorDetail.builder().pointer(parameterName).reason(error.getDefaultMessage()).build());
			});
		}

		return ResponseEntity.status(BAD_REQUEST).body(this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors));
	}

	// Process @Validated
	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
	public ProblemDetail handleJakartaConstraintViolationException(final jakarta.validation.ConstraintViolationException ex, final WebRequest request) {
		log.info(ex.getMessage(), ex);

		final List<ErrorDetail> errors = new ArrayList<>();
		for (final var violation : ex.getConstraintViolations()) {
			errors.add(ErrorDetail.builder()
				// Get specific parameter name
				.pointer(((PathImpl) violation.getPropertyPath()).getLeafNode().getName()).reason(violation.getMessage())
				// .pointer(
				//     violation.getInvalidValue() != null
				//         ? violation.getInvalidValue().toString()
				//         : StringUtils.EMPTY)
				.build());
		}

		return this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors);
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({ /**org.hibernate.exception.ConstraintViolationException.class,**/DataIntegrityViolationException.class, BatchUpdateException.class, PersistenceException.class})
	public ProblemDetail handlePersistenceException(final Exception ex, final WebRequest request) {
		log.info(ex.getMessage(), ex);

		final String cause = NestedExceptionUtils.getMostSpecificCause(ex).getLocalizedMessage();
		final String errorDetail = this.extractPersistenceDetails(cause);
		return this.buildProblemDetail(BAD_REQUEST, errorDetail);
	}

	/*
	 *  When authorizing user at controller or service layer using @PreAuthorize it throws
	 * AccessDeniedException, and it's a developer's responsibility to catch it
	 * */
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	public ProblemDetail handleAccessDeniedException(final Exception ex, final WebRequest request) {
		log.info(ex.getMessage(), ex);
		return this.buildProblemDetail(HttpStatus.FORBIDDEN, null);
	}

	/*
	 * Catch API defined exceptions
	 *
	 * */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(BusinessException.class)
	public ProblemDetail businessException(final BusinessException ex) {
		log.info(ex.getMessage(), ex);

		// if (ex.getHttpStatus().is5xxServerError()) {
		//   this.slack.notify(format("[API] InternalServerError: %s", ex.getMessage()));
		// }

		return this.buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	/*
	 * Fallback, catch all unknown API exceptions
	 *  */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public ProblemDetail handleAllExceptions(final Throwable ex, final WebRequest request) {
		log.warn(format("%s", ex.getMessage()), ex);
		// this.slack.notify(format("[API] InternalServerError: %s", ex.getMessage()));
		return this.buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, API_DEFAULT_ERROR_MESSAGE);
	}

	private ProblemDetail buildProblemDetail(final HttpStatus status, final String detail) {
		return this.buildProblemDetail(status, detail, emptyList());
	}

	private ProblemDetail buildProblemDetail(final HttpStatus status, final String detail, final List<ErrorDetail> errors) {
		final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, StringUtils.normalizeSpace(detail));
		if (!CollectionUtils.isEmpty(errors)) {
			problemDetail.setProperty("errors", errors);
		}

		problemDetail.setProperty("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));

		return problemDetail;
	}

	private String extractPersistenceDetails(final String cause) {
		String details = API_DEFAULT_ERROR_MESSAGE;

		// Example: ERROR: duplicate key value violates unique constraint "company_slug_key"  Detail:
		// Key (slug)=(bl8lo0d) already exists.
		if (cause.contains("Detail")) {
			final List<String> matchList = new ArrayList<>();
			// find database values between "()"
			final Pattern pattern = Pattern.compile("\\((.*?)\\)");
			final Matcher matcher = pattern.matcher(cause);

			// Creates list ["slug", "bl8lo0d"]
			while (matcher.find()) {
				matchList.add(matcher.group(1));
			}

			if (matchList.size() == 2) {
				final String key = matchList.get(0);
				final String value = matchList.get(1);
				// Gets the message after the last ")"
				final String message = cause.substring(cause.lastIndexOf(")") + 1);

				// return errorMessage: slug 'bl8lo0d'  already exists.
				details = format("%s '%s' %s", key, value, message);
			}
		}

		return details;
	}

}
