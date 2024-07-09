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
package com.chensoul.sharedlib.validation.validator;

import com.chensoul.sharedlib.validation.EnumCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 枚举校验器
 *
 * @author zhijun.chen
 * @since 0.0.1
 */
public class EnumCheckValidator implements ConstraintValidator<EnumCheck, Integer> {

	private Class<?> enumClass;

	private String propertyKey;

	@Override
	public void initialize(EnumCheck constraintAnnotation) {
		enumClass = constraintAnnotation.enumClass();
		propertyKey = constraintAnnotation.propertyKey();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (null == value) {
			return Boolean.TRUE;
		}

		AtomicBoolean isValid = new AtomicBoolean(false);
		for (Object enumConstant : enumClass.getEnumConstants()) {
			for (Field field : FieldUtils.getAllFields(enumClass)) {
				if (field.getName().equals(propertyKey)) {
					try {
						if ((!Modifier.isPublic(field.getModifiers()) ||
							 !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
							 Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
							field.setAccessible(true);
						}
						if (field.get(enumConstant).equals(value)) {
							isValid.set(true);
							break;
						}
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return isValid.get();
	}
}
