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
package com.chensoul.monolith.infrastructure.validation;

import com.chensoul.monolith.common.exception.BusinessException;
import com.chensoul.monolith.common.exception.IncorrectParameterException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.util.StringUtils;

/**
 * Validators
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public class Validators {
	/**
	 * This method validate <code>String</code> string. If string is invalid than throw
	 * <code>IncorrectParameterException</code> exception
	 *
	 * @param val          the val
	 * @param errorMessage the error message for exception
	 */
	public static void validateString(String val, String errorMessage) {
		if (val == null || val.isEmpty()) {
			throw new IncorrectParameterException(errorMessage);
		}
	}

	/*
	 * This method validate <code>String</code> string. If string is invalid than throw
	 * <code>IncorrectParameterException</code> exception
	 *
	 * @param val                       the value
	 * @param errorMessageFunction      the error message function that apply value
	 */
	public static void validateString(String val, Function<String, String> errorMessageFunction) {
		if (val == null || val.isEmpty()) {
			throw new IncorrectParameterException(errorMessageFunction.apply(val));
		}
	}

	/**
	 * This method validate <code>long</code> value. If value isn't positive than throw
	 * <code>IncorrectParameterException</code> exception
	 *
	 * @param val          the val
	 * @param errorMessage the error message for exception
	 */
	public static void validatePositiveNumber(long val, String errorMessage) {
		if (val <= 0) {
			throw new IncorrectParameterException(errorMessage);
		}
	}

	/**
	 * This method validate <code>UUID</code> id. If id is null than throw
	 * <code>IncorrectParameterException</code> exception
	 *
	 * @param id           the id
	 * @param errorMessage the error message for exception
	 */
	public static void validateId(Serializable id, String errorMessage) {
		if (id == null) {
			throw new IncorrectParameterException(errorMessage);
		}
	}

	public static void validateId(Serializable id, Function<Serializable, String> errorMessageFunction) {
		if (id == null) {
			throw new IncorrectParameterException(errorMessageFunction.apply(id));
		}
	}

	/**
	 * This method validate list of <code>UUIDBased</code> ids. If at least one of the ids is null than throw
	 * <code>IncorrectParameterException</code> exception
	 *
	 * @param ids          the list of ids
	 * @param errorMessage the error message for exception
	 */
	public static void validateIds(List<? extends Serializable> ids, String errorMessage) {
		if (ids == null || ids.isEmpty()) {
			throw new DataValidationException(errorMessage);
		} else {
			for (Serializable id : ids) {
				validateId(id, errorMessage);
			}
		}
	}

	public static <T> T checkNotNull(T reference) throws BusinessException {
		return checkNotNull(reference, "Requested item wasn't found!");
	}

	public static <T> T checkNotNull(T reference, String notFoundMessage) throws BusinessException {
		if (reference == null) {
			throw new IncorrectParameterException(notFoundMessage);
		}
		return reference;
	}

	public static <T> T checkNotNull(Optional<T> reference) throws BusinessException {
		return checkNotNull(reference, "Requested item wasn't found!");
	}

	public static <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws BusinessException {
		if (reference.isPresent()) {
			return reference.get();
		} else {
			throw new IncorrectParameterException(notFoundMessage);
		}
	}

	public static void checkParameter(String name, String param) {
		if (StringUtils.isEmpty(param)) {
			throw new IncorrectParameterException("Parameter '" + name + "' can't be empty!");
		}
	}

	public static void checkParameter(String name, Long param) throws BusinessException {
		if (param == null) {
			throw new IncorrectParameterException("Parameter '" + name + "' can't be empty!");
		}
	}
}
