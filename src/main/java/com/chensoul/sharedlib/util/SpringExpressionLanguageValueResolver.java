package com.chensoul.sharedlib.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Slf4j
public class SpringExpressionLanguageValueResolver implements Function {
	private static final int HOUR_23 = 23;

	private static final int MINUTE_59 = 59;

	private static final int SECOND_59 = 59;

	private static final ParserContext PARSER_CONTEXT = new TemplateParserContext("${", "}");

	private static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser(
		new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, SpringExpressionLanguageValueResolver.class.getClassLoader())
	);

	private static SpringExpressionLanguageValueResolver INSTANCE;

	private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

	protected SpringExpressionLanguageValueResolver() {
		val properties = System.getProperties();
		evaluationContext.setVariable("systemProperties", properties);
		evaluationContext.setVariable("sysProps", properties);

		val environment = System.getenv();
		evaluationContext.setVariable("environmentVars", environment);
		evaluationContext.setVariable("environmentVariables", environment);
		evaluationContext.setVariable("envVars", environment);
		evaluationContext.setVariable("env", environment);

		evaluationContext.setVariable("tempDir", FileUtils.getTempDirectoryPath());
		evaluationContext.setVariable("zoneId", ZoneId.systemDefault().getId());
	}

	/**
	 * Gets instance of the resolver as a singleton.
	 *
	 * @return the instance
	 */
	public static SpringExpressionLanguageValueResolver getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SpringExpressionLanguageValueResolver();
		}
		INSTANCE.initializeDynamicVariables();
		return INSTANCE;
	}

	/**
	 * Resolve string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public String resolve(final String value) {
		if (StringUtils.isNotBlank(value)) {
			log.trace("Parsing expression as [{}]", value);
			val expression = EXPRESSION_PARSER.parseExpression(value, PARSER_CONTEXT);
			val result = expression.getValue(evaluationContext, String.class);
			log.trace("Parsed expression result is [{}]", result);
			return result;
		}
		return value;
	}

	@Override
	public Object apply(final Object o) {
		return resolve(o.toString());
	}

	private void initializeDynamicVariables() {
		evaluationContext.setVariable("randomNumber2", RandomStringUtils.randomNumeric(2));
		evaluationContext.setVariable("randomNumber4", RandomStringUtils.randomNumeric(4));
		evaluationContext.setVariable("randomNumber6", RandomStringUtils.randomNumeric(6));
		evaluationContext.setVariable("randomNumber8", RandomStringUtils.randomNumeric(8));

		evaluationContext.setVariable("randomString4", RandomStringUtils.randomAlphabetic(4));
		evaluationContext.setVariable("randomString6", RandomStringUtils.randomAlphabetic(6));
		evaluationContext.setVariable("randomString8", RandomStringUtils.randomAlphabetic(8));

		evaluationContext.setVariable("uuid", UUID.randomUUID().toString());

		evaluationContext.setVariable("localDateTime", LocalDateTime.now(ZoneId.systemDefault()).toString());
		evaluationContext.setVariable("localDateTimeUtc", LocalDateTime.now(Clock.systemUTC()).toString());

		val localStartWorkDay = LocalDate.now(ZoneId.systemDefault()).atStartOfDay().plusHours(8);
		evaluationContext.setVariable("localStartWorkDay", localStartWorkDay.toString());
		evaluationContext.setVariable("localEndWorkDay", localStartWorkDay.plusHours(9).toString());

		val localStartDay = LocalDate.now(ZoneId.systemDefault()).atStartOfDay();
		evaluationContext.setVariable("localStartDay", localStartDay.toString());
		evaluationContext.setVariable("localEndDay",
			localStartDay.plusHours(HOUR_23).plusMinutes(MINUTE_59).plusSeconds(SECOND_59).toString());

		evaluationContext.setVariable("localDate", LocalDate.now(ZoneId.systemDefault()).toString());
		evaluationContext.setVariable("localDateUtc", LocalDate.now(Clock.systemUTC()).toString());

		evaluationContext.setVariable("zonedDateTime", ZonedDateTime.now(ZoneId.systemDefault()).toString());
		evaluationContext.setVariable("zonedDateTimeUtc", ZonedDateTime.now(Clock.systemUTC()).toString());
	}
}
