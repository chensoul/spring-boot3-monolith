package com.chensoul.sharedlib.util.reflect;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public class ParameterizedTypeImpl implements ParameterizedType {

	private final Type[] actualTypeArguments;

	private final Class<?> rawType;

	private final Type ownerType;

	private ParameterizedTypeImpl(Class<?> rawType,
								  Type[] actualTypeArguments,
								  Type ownerType) {
		this.actualTypeArguments = actualTypeArguments;
		this.rawType = rawType;
		this.ownerType = (ownerType != null) ? ownerType : rawType.getDeclaringClass();
		validateConstructorArguments();
	}

	public static ParameterizedTypeImpl of(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
		return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
	}

	private void validateConstructorArguments() {
		TypeVariable<?>[] formals = rawType.getTypeParameters();
		// check correct arity of actual type args
		if (formals.length != actualTypeArguments.length) {
			throw new MalformedParameterizedTypeException();
		}
		for (int i = 0; i < actualTypeArguments.length; i++) {
			// check actuals against formals' bounds
		}
	}

	/**
	 * Returns an array of <tt>Type</tt> objects representing the actual type
	 * arguments to this type.
	 *
	 * <p>Note that in some cases, the returned array be empty. This can occur
	 * if this type represents a non-parameterized type nested within
	 * a parameterized type.
	 *
	 * @return an array of <tt>Type</tt> objects representing the actual type
	 * arguments to this type
	 * @since 1.5
	 */
	public Type[] getActualTypeArguments() {
		return actualTypeArguments.clone();
	}

	/**
	 * <p>Getter for the field <code>rawType</code>.</p>
	 *
	 * @return a {@link Class} object
	 */
	public Class<?> getRawType() {
		return rawType;
	}


	/**
	 * <p>Getter for the field <code>ownerType</code>.</p>
	 *
	 * @return a {@link Type} object
	 */
	public Type getOwnerType() {
		return ownerType;
	}

	/*
	 * From the JavaDoc for java.lang.reflect.ParameterizedType
	 * "Instances of classes that implement this interface must
	 * implement an equals() method that equates any two instances
	 * that share the same generic type declaration and have equal
	 * type parameters."
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ParameterizedType) {
			// Check that information is equivalent
			ParameterizedType that = (ParameterizedType) o;

			if (this == that)
				return true;

			Type thatOwner = that.getOwnerType();
			Type thatRawType = that.getRawType();

			if (false) { // Debugging
				boolean ownerEquality = (ownerType == null ?
					thatOwner == null :
					ownerType.equals(thatOwner));
				boolean rawEquality = (rawType == null ?
					thatRawType == null :
					rawType.equals(thatRawType));

				boolean typeArgEquality = Arrays.equals(actualTypeArguments, // avoid clone
					that.getActualTypeArguments());
				for (Type t : actualTypeArguments) {
					System.out.printf("\t\t%s%s%n", t, t.getClass());
				}

				System.out.printf("\towner %s\traw %s\ttypeArg %s%n",
					ownerEquality, rawEquality, typeArgEquality);
				return ownerEquality && rawEquality && typeArgEquality;
			}

			return
				Objects.equals(ownerType, thatOwner) &&
				Objects.equals(rawType, thatRawType) &&
				Arrays.equals(actualTypeArguments, // avoid clone
					that.getActualTypeArguments());
		} else
			return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(actualTypeArguments) ^
			   Objects.hashCode(ownerType) ^
			   Objects.hashCode(rawType);
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link String} object
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (ownerType != null) {
			if (ownerType instanceof Class)
				sb.append(((Class) ownerType).getName());
			else
				sb.append(ownerType.toString());

			sb.append("$");

			if (ownerType instanceof ParameterizedTypeImpl) {
				// Find simple name of nested type by removing the
				// shared prefix with owner.
				sb.append(rawType.getName().replace(((ParameterizedTypeImpl) ownerType).rawType.getName() + "$", ""));
			} else
				sb.append(rawType.getSimpleName());
		} else
			sb.append(rawType.getName());

		if (actualTypeArguments != null &&
			actualTypeArguments.length > 0) {
			sb.append("<");
			boolean first = true;
			for (Type t : actualTypeArguments) {
				if (!first)
					sb.append(", ");
				sb.append(t.getTypeName());
				first = false;
			}
			sb.append(">");
		}

		return sb.toString();
	}
}
