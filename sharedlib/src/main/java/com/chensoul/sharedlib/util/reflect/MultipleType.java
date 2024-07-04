package com.chensoul.sharedlib.util.reflect;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public class MultipleType {

	private final Type[] types;

	private MultipleType(Type... types) {
		this.types = types;
	}

	public static MultipleType of(Type one, Type two) {
		return new MultipleType(one, two);
	}

	/**
	 * @param one    a {@link Type} object
	 * @param two    a {@link Type} object
	 * @param others a {@link Type} object
	 * @return a {@link com.chensoul.reflect.MultipleType} object
	 */
	public static MultipleType of(Type one, Type two, Type... others) {
		Type[] types = new Type[2 + others.length];
		ArrayUtils.add(types, one);
		ArrayUtils.add(types, two);
		ArrayUtils.addAll(types, others);
		return new MultipleType(types);
	}

	@Override
	public int hashCode() {
		return Objects.hash(types);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultipleType that = (MultipleType) o;
		return Arrays.equals(types, that.types);
	}

	@Override
	public String toString() {
		return "MultipleType : " + Arrays.toString(types);
	}
}
