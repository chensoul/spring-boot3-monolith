package com.chensoul.sharedlib.validation.validator;

import com.chensoul.sharedlib.validation.Length;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Length Validator
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
public class LengthValidator implements ConstraintValidator<Length, Object> {
	private int max;

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		String stringValue;
		if (value instanceof CharSequence || value instanceof JsonNode) {
			stringValue = value.toString();
		} else {
			return true;
		}
		if (StringUtils.isEmpty(stringValue)) {
			return true;
		}
		return stringValue.length() <= max;
	}

	@Override
	public void initialize(Length constraintAnnotation) {
		this.max = constraintAnnotation.max();
	}
}
