package com.chensoul.sharedlib.validation.validator;

import com.chensoul.sharedlib.validation.NoXss;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * NoXss Validator
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
public class NoXssValidator implements ConstraintValidator<NoXss, Object> {
	private static final AntiSamy xssChecker = new AntiSamy();
	private static final Policy xssPolicy;

	static {
		xssPolicy = Optional.ofNullable(NoXssValidator.class.getClassLoader().getResourceAsStream("xss-policy.xml"))
			.map(inputStream -> {
				try {
					return Policy.getInstance(inputStream);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			})
			.orElseThrow(() -> new IllegalStateException("XSS policy file not found"));
	}

	public static boolean isValid(String stringValue) {
		if (stringValue.isEmpty()) {
			return true;
		}
		try {
			return xssChecker.scan(stringValue, xssPolicy).getNumberOfErrors() == 0;
		} catch (ScanException | PolicyException e) {
			return false;
		}
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
		String stringValue;
		if (value instanceof CharSequence || value instanceof JsonNode) {
			stringValue = value.toString();
		} else {
			return true;
		}
		return isValid(stringValue);
	}

}
