package com.chensoul.sharedlib.util.reflect;

import com.chensoul.sharedlib.util.lang.function.CheckedSupplier;
import static com.chensoul.sharedlib.util.lang.function.Predicates.and;
import static com.chensoul.sharedlib.util.lang.function.Streams.filter;
import static com.chensoul.sharedlib.util.reflect.AccessibleObjectUtils.execute;
import java.lang.reflect.Field;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The Java Reflection {@link Field} Utility class
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public abstract class FieldUtils {

	private FieldUtils() {
	}

	/**
	 * Find the specified objects' declared {@link Field} by its' name
	 *
	 * @param object    the {@link Object object} to find
	 * @param fieldName the name of {@link Field field}
	 * @return null if not found
	 */
	public static Field findField(Object object, String fieldName) {
		return findField(object.getClass(), fieldName);
	}

	/**
	 * Find the declared {@link Field} by its' name
	 *
	 * @param klass     {@link Class class} to find
	 * @param fieldName the name of {@link Field field}
	 * @return null if not found
	 */
	public static Field findField(Class<?> klass, String fieldName) {
		if (klass == null || Object.class.equals(klass)) {
			return null;
		}
		Field field = null;
		try {
			field = klass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// ignore, try the super class
			field = findField(klass.getSuperclass(), fieldName);
		}
		return field;
	}

	/**
	 * Find the declared {@link Field} by its' name
	 *
	 * @param klass     {@link Class class} to find
	 * @param fieldName the name of {@link Field field}
	 * @param fieldType the {@link Class type} of {@link Field field}
	 * @return null if not found
	 */
	public static Field findField(Class<?> klass, String fieldName, Class<?> fieldType) {
		return findField(klass, fieldName, field -> Objects.equals(fieldType, field.getType()));
	}

	/**
	 * Find the declared {@link Field} by its' name
	 *
	 * @param klass      {@link Class class} to find
	 * @param fieldName  the name of {@link Field field}
	 * @param predicates zero or more {@link Predicate}
	 * @return null if not found
	 */
	public static Field findField(Class<?> klass, String fieldName, Predicate<Field>... predicates) {
		Field field = findField(klass, fieldName);
		return and(predicates).test(field) ? field : null;
	}

	/**
	 * Get the static {@link Field} Value
	 *
	 * @param klass     {@link Class class} to find
	 * @param fieldName the name of {@link Field field}
	 * @param <T>       a T class
	 * @return <code>null</code> if <code>field</code> is <code>null</code> or get failed
	 */
	public static <T> T getStaticFieldValue(Class<?> klass, String fieldName) {
		Field field = findField(klass, fieldName);
		return getStaticFieldValue(field);
	}

	/**
	 * Get the static {@link Field} Value
	 *
	 * @param field {@link Field}
	 * @param <T>   a T class
	 * @return <code>null</code> if <code>field</code> is <code>null</code> or get failed
	 */
	public static <T> T getStaticFieldValue(Field field) {
		return getFieldValue(null, field);
	}

	/**
	 * Set the value to static {@link Field}
	 *
	 * @param klass      the class declared the field
	 * @param fieldName  the name of {@link Field}
	 * @param fieldValue the value of {@link Field}
	 */
	public static void setStaticFieldValue(Class<?> klass, String fieldName, Object fieldValue) {
		Field field = findField(klass, fieldName);
		setFieldValue(null, field, fieldValue);
	}

	/**
	 * <p>getAllFields.</p>
	 *
	 * @param declaredClass a {@link Class} object
	 * @param fieldFilters  a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Field> getAllFields(Class<?> declaredClass, Predicate<Field>... fieldFilters) {
		Set<Field> allFields = new LinkedHashSet<>(asList(declaredClass.getFields()));
		for (Class superType : ClassUtils.getAllInheritedTypes(declaredClass)) {
			allFields.addAll(asList(superType.getFields()));
		}
		return filter(allFields, and(fieldFilters));
	}

	/**
	 * <p>getAllDeclaredFields.</p>
	 *
	 * @param declaredClass a {@link Class} object
	 * @param fieldFilters  a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Field> getAllDeclaredFields(Class<?> declaredClass, Predicate<Field>... fieldFilters) {
		Set<Field> allDeclaredFields = new LinkedHashSet<>(asList(declaredClass.getDeclaredFields()));
		for (Class superType : ClassUtils.getAllInheritedTypes(declaredClass)) {
			allDeclaredFields.addAll(asList(superType.getDeclaredFields()));
		}
		return filter(allDeclaredFields, and(fieldFilters));
	}

	/**
	 * Like the {@link Class#getDeclaredField(String)} method without throwing any {@link Exception}
	 *
	 * @param declaredClass the declared class
	 * @param fieldName     the name of {@link Field}
	 * @return if can't be found, return <code>null</code>
	 */
	public static Field getDeclaredField(Class<?> declaredClass, String fieldName) {
		return CheckedSupplier.unchecked(() -> declaredClass.getDeclaredField(fieldName)).get();
	}

	/**
	 * Get the value of the specified {@link Field}
	 *
	 * @param object    the object whose field should be modified
	 * @param fieldName the name of {@link Field}
	 * @param <T>       a T class
	 * @return the value of  the specified {@link Field}
	 */
	public static <T> T getFieldValue(Object object, String fieldName) {
		return getFieldValue(object, findField(object, fieldName));
	}


	/**
	 * Get {@link Field} Value
	 *
	 * @param object       the object whose field should be modified
	 * @param fieldName    field name
	 * @param <T>          field type
	 * @param defaultValue default value
	 * @return {@link Field} Value
	 */
	public static <T> T getFieldValue(Object object, String fieldName, T defaultValue) {
		T value = getFieldValue(object, fieldName);
		return value != null ? value : defaultValue;
	}

	/**
	 * Get {@link Field} Value
	 *
	 * @param object    the object whose field should be modified
	 * @param fieldName field name
	 * @param fieldType field type
	 * @param <T>       field type
	 * @return {@link Field} Value
	 */
	public static <T> T getFieldValue(Object object, String fieldName, Class<T> fieldType) {
		Field field = findField(object.getClass(), fieldName, fieldType);
		return getFieldValue(object, field);
	}

	/**
	 * Get the value of the specified {@link Field}
	 *
	 * @param object the object whose field should be modified
	 * @param field  {@link Field}
	 * @param <T>    a T class
	 * @return the value of  the specified {@link Field}
	 */
	public static <T> T getFieldValue(Object object, Field field) {
		return (T) execute(field, () -> {
			return field.get(object);
		});
	}

	/**
	 * Set the value for the specified {@link Field}
	 *
	 * @param object    the object whose field should be modified
	 * @param fieldName the name of {@link Field}
	 * @param value     the value of field to be set
	 * @param <T>       a T class
	 * @return the previous value of the specified {@link Field}
	 */
	public static <T> T setFieldValue(Object object, String fieldName, T value) {
		return setFieldValue(object, findField(object, fieldName), value);
	}

	/**
	 * Set the value for the specified {@link Field}
	 *
	 * @param object the object whose field should be modified
	 * @param field  {@link Field}
	 * @param value  the value of field to be set
	 * @param <T>    a T class
	 * @return the previous value of the specified {@link Field}
	 */
	public static <T> T setFieldValue(Object object, Field field, T value) {
		return execute(field, () -> {
			Object previousValue = null;
			try {
				previousValue = field.get(object);
				if (!Objects.equals(previousValue, value)) {
					field.set(object, value);
				}
			} catch (IllegalAccessException ignored) {
			}
			return (T) previousValue;
		});
	}

	/**
	 * Assert Field type match
	 *
	 * @param object       Object
	 * @param fieldName    field name
	 * @param expectedType expected type
	 * @throws IllegalArgumentException if any.
	 */
	public static void assertFieldMatchType(Object object, String fieldName, Class<?> expectedType) throws IllegalArgumentException {
		Class<?> type = object.getClass();
		Field field = findField(type, fieldName);
		Class<?> fieldType = field.getType();
		if (!expectedType.isAssignableFrom(fieldType)) {
			String message = String.format("The type[%s] of field[%s] in Class[%s] can't match expected type[%s]", fieldType.getName(), fieldName, type.getName(), expectedType.getName());
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Enable field to be accessible
	 *
	 * @param field {@link Field}
	 */
	public static void enableAccessible(Field field) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
	}
}
