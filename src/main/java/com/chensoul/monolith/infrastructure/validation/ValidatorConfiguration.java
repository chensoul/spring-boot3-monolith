package com.chensoul.monolith.infrastructure.validation;

import com.chensoul.monolith.infrastructure.validation.validator.LengthValidator;
import com.chensoul.monolith.infrastructure.validation.validator.NoXssValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Validator Configuration
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class ValidatorConfiguration {

	private static Validator fieldsValidators;

	static {
		initializeValidators();
	}

	public static void validateFields(Object data) {
		validateFields(data, "");
	}

	public static void validateFields(Object data, String errorPrefix) {
		Set<ConstraintViolation<Object>> constraintsViolations = fieldsValidators.validate(data);
		if (!constraintsViolations.isEmpty()) {
			throw new DataValidationException(errorPrefix + getErrorMessage(constraintsViolations));
		}
	}

	public static String getErrorMessage(Collection<ConstraintViolation<Object>> constraintsViolations) {
		return constraintsViolations.stream()
			.map(ValidatorConfiguration::getErrorMessage)
			.distinct().sorted().collect(Collectors.joining(", "));
	}

	public static String getErrorMessage(ConstraintViolation<Object> constraintViolation) {
		ConstraintDescriptor<?> constraintDescriptor = constraintViolation.getConstraintDescriptor();
		String property = (String) constraintDescriptor.getAttributes().get("fieldName");
		if (StringUtils.isEmpty(property) && !(constraintDescriptor.getAnnotation() instanceof AssertTrue)) {
			property = constraintViolation.getPropertyPath().iterator().next().toString();
		}

		String error = "";
		if (StringUtils.isNotEmpty(property)) {
			error += property + " ";
		}
//		error += constraintViolation.getMessage();
		return constraintViolation.getMessage();
	}

	private static void initializeValidators() {
		HibernateValidatorConfiguration validatorConfiguration = Validation.byProvider(HibernateValidator.class).configure();

		ConstraintMapping constraintMapping = getCustomConstraintMapping();
		validatorConfiguration.addMapping(constraintMapping);

		fieldsValidators = validatorConfiguration.buildValidatorFactory().getValidator();
	}

	private static ConstraintMapping getCustomConstraintMapping() {
		ConstraintMapping constraintMapping = new DefaultConstraintMapping(null);
		constraintMapping.constraintDefinition(NoXss.class).validatedBy(NoXssValidator.class);
		constraintMapping.constraintDefinition(Length.class).validatedBy(LengthValidator.class);
		return constraintMapping;
	}

	@Bean
	public LocalValidatorFactoryBean validatorFactoryBean() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setConfigurationInitializer(configuration -> {
			((ConfigurationImpl) configuration).addMapping(getCustomConstraintMapping());
		});
		return localValidatorFactoryBean;
	}

}
