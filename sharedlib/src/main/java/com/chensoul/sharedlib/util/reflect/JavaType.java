package com.chensoul.sharedlib.util.reflect;

import static com.chensoul.sharedlib.util.reflect.TypeUtils.EMPTY_TYPE;
import static com.chensoul.sharedlib.util.reflect.TypeUtils.asParameterizedType;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Encapsulates a Java Type(Immutable), providing the features:
 * <ul>
 *     <li>{@link #getSuperType() supertypes}</li>
 *     <li>{@link #getInterfaces() interfaces}</li>
 *     <li>{@link #getGenericTypes() generic parameters}</li>
 *     <li>{@link #getRawType() raw type}</li>
 *     <li>{@link #getSource() the tracing source}</li>
 *     <li>{@link #getRootSource() the root source}</li>
 *     <li>{@link #as(Class) cast to the super type or interface}</li>
 * </ul>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Type
 * @since 1.0.0
 */
public class JavaType implements Serializable {

	/**
	 * Constant <code>EMPTY_ARRAY</code>
	 */
	public static final JavaType[] EMPTY_ARRAY = new JavaType[0];

	/**
	 * Constant <code>OBJECT_TYPE</code>
	 */
	public static final JavaType OBJECT_TYPE = new JavaType(Object.class, Kind.CLASS);

	private final Type type;

	private final transient Kind kind;

	// Local cache fields

	private volatile JavaType source;

	private volatile JavaType superType;

	private volatile JavaType[] interfaces;

	private volatile JavaType[] genericTypes;

	/**
	 * <p>Constructor for JavaType.</p>
	 *
	 * @param type a {@link Type} object
	 */
	protected JavaType(Type type) {
		this(type, Kind.valueOf(type));
	}

	/**
	 * <p>Constructor for JavaType.</p>
	 *
	 * @param type a {@link Type} object
	 * @param kind a {@link JavaType.Kind} object
	 */
	protected JavaType(Type type, Kind kind) {
		this(type, kind, null);
	}

	/**
	 * <p>Constructor for JavaType.</p>
	 *
	 * @param type   a {@link Type} object
	 * @param source a {@link JavaType} object
	 */
	protected JavaType(Type type, JavaType source) {
		this(type, Kind.valueOf(type), source);
	}

	/**
	 * <p>Constructor for JavaType.</p>
	 *
	 * @param type   a {@link Type} object
	 * @param kind   a {@link JavaType.Kind} object
	 * @param source a {@link JavaType} object
	 */
	protected JavaType(Type type, Kind kind, JavaType source) {
		this.type = type;
		this.kind = kind;
		this.source = source;
	}

	private static JavaType resolveArgumentGenericType(Type[] typeArguments, JavaType argumentType, int index) {
		JavaType genericType = argumentType;
		TypeVariable typeVariable = argumentType.toTypeVariable();
		if (typeVariable != null) {
			JavaType source = argumentType.getRootSource();
			GenericDeclaration declaration = typeVariable.getGenericDeclaration();
			if (matches(typeArguments, declaration)) {
				JavaType[] genericTypes = source.getGenericTypes();
				genericType = index <= genericTypes.length - 1 ? genericTypes[index] : null;
			}
		}
		return genericType;
	}

	/**
	 * <p>from.</p>
	 *
	 * @param field a {@link Field} object
	 * @return a {@link JavaType} object
	 */
	public static JavaType from(Field field) {
		return from(field.getGenericType());
	}

	/**
	 * <p>fromMethodReturnType.</p>
	 *
	 * @param method a {@link Method} object
	 * @return a {@link JavaType} object
	 */
	public static JavaType fromMethodReturnType(Method method) {
		Type genericReturnType = method.getGenericReturnType();
		return from(genericReturnType);
	}

	/**
	 * <p>fromMethodParameters.</p>
	 *
	 * @param method a {@link Method} object
	 * @return an array of {@link JavaType} objects
	 */
	public static JavaType[] fromMethodParameters(Method method) {
		Type[] genericParameterType = method.getGenericParameterTypes();
		return from(genericParameterType);
	}

	/**
	 * <p>fromMethodParameter.</p>
	 *
	 * @param method         a {@link Method} object
	 * @param parameterIndex a int
	 * @return a {@link JavaType} object
	 */
	public static JavaType fromMethodParameter(Method method, int parameterIndex) {
		Type genericParameterType = method.getGenericParameterTypes()[parameterIndex];
		return from(genericParameterType);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param targetClass a {@link Class} object
	 * @return a {@link JavaType} object
	 */
	public static JavaType from(Class<?> targetClass) {
		return from(targetClass, Kind.CLASS);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link JavaType} object
	 */
	public static JavaType from(Type type) {
		return new JavaType(type);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param type a {@link Type} object
	 * @param kind a {@link JavaType.Kind} object
	 * @return a {@link JavaType} object
	 */
	protected static JavaType from(Type type, Kind kind) {
		return new JavaType(type, kind);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param type   a {@link Type} object
	 * @param source a {@link JavaType} object
	 * @return a {@link JavaType} object
	 */
	protected static JavaType from(Type type, JavaType source) {
		return new JavaType(type, source);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param type   a {@link Type} object
	 * @param kind   a {@link JavaType.Kind} object
	 * @param source a {@link JavaType} object
	 * @return a {@link JavaType} object
	 */
	protected static JavaType from(Type type, Kind kind, JavaType source) {
		return new JavaType(type, kind, source);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param types an array of {@link Type} objects
	 * @return an array of {@link JavaType} objects
	 */
	protected static JavaType[] from(Type[] types) {
		return from(types, null);
	}

	/**
	 * <p>from.</p>
	 *
	 * @param types  an array of {@link Type} objects
	 * @param source a {@link JavaType} object
	 * @return an array of {@link JavaType} objects
	 */
	protected static JavaType[] from(Type[] types, JavaType source) {
		int length = types == null ? 0 : types.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		JavaType[] javaTypes = new JavaType[length];
		for (int i = 0; i < length; i++) {
			javaTypes[i] = from(types[i], source);
		}
		return javaTypes;
	}

	private static boolean matches(Class<?> targetClass, Type typeToMatch) {
		Kind kind = Kind.valueOf(typeToMatch);
		Type rawType = kind.getRawType(typeToMatch);
		return Objects.equals(targetClass, rawType);
	}

	private static boolean matches(Type[] typeArguments, GenericDeclaration declaration) {
		TypeVariable[] typeParameters = declaration.getTypeParameters();
		if (Arrays.deepEquals(typeArguments, typeParameters)) {
			return true;
		} else if (declaration instanceof Class) { // To find the super class
			Class declaredClass = (Class) declaration;
			return matches(typeArguments, declaredClass.getSuperclass());
		}
		return false;
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link Type} object
	 */
	public Type getType() {
		return type;
	}

	/**
	 * <p>Getter for the field <code>kind</code>.</p>
	 *
	 * @return a {@link JavaType.Kind} object
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * <p>Getter for the field <code>source</code>.</p>
	 *
	 * @return a {@link JavaType} object
	 */
	public JavaType getSource() {
		return this.source;
	}

	/**
	 * <p>getRootSource.</p>
	 *
	 * @return a {@link JavaType} object
	 */
	@Nullable
	public JavaType getRootSource() {
		JavaType currentSource = this.source;
		if (currentSource == null) {
			return null;
		}
		JavaType rootSource;
		do {
			rootSource = currentSource.source;
			if (rootSource == null) {
				rootSource = currentSource;
				break;
			} else {
				currentSource = currentSource.source;
			}
		} while (true);

		return rootSource;
	}

	/**
	 * <p>getRawType.</p>
	 *
	 * @return a {@link Type} object
	 */
	@Nullable
	public Type getRawType() {
		return this.kind.getRawType(this.type);
	}

	/**
	 * <p>isSource.</p>
	 *
	 * @return a boolean
	 */
	public boolean isSource() {
		return this.source == null;
	}

	/**
	 * <p>isRootSource.</p>
	 *
	 * @return a boolean
	 */
	public boolean isRootSource() {
		return getRootSource() == null;
	}

	/**
	 * <p>isClass.</p>
	 *
	 * @return a boolean
	 */
	public boolean isClass() {
		return Kind.CLASS.equals(this.kind);
	}

	/**
	 * <p>isParameterizedType.</p>
	 *
	 * @return a boolean
	 */
	public boolean isParameterizedType() {
		return Kind.PARAMETERIZED_TYPE.equals(this.kind);
	}

	/**
	 * <p>isTypeVariable.</p>
	 *
	 * @return a boolean
	 */
	public boolean isTypeVariable() {
		return Kind.TYPE_VARIABLE.equals(this.kind);
	}

	/**
	 * <p>isWildCardType.</p>
	 *
	 * @return a boolean
	 */
	public boolean isWildCardType() {
		return Kind.WILDCARD_TYPE.equals(this.kind);
	}

	/**
	 * <p>Getter for the field <code>superType</code>.</p>
	 *
	 * @return a {@link JavaType} object
	 */
	@Nullable
	public JavaType getSuperType() {
		JavaType superType = this.superType;
		if (superType == null) {
			superType = resolveSuperType();
			this.superType = superType;
		}
		return superType;
	}

	/**
	 * <p>resolveSuperType.</p>
	 *
	 * @return a {@link JavaType} object
	 */
	protected JavaType resolveSuperType() {
		Type superType = kind.getSuperType(type);
		return from(superType, this);
	}

	/**
	 * <p>Getter for the field <code>interfaces</code>.</p>
	 *
	 * @return an array of {@link JavaType} objects
	 */
	public JavaType[] getInterfaces() {
		JavaType[] interfaces = this.interfaces;
		if (interfaces == null) {
			interfaces = resolveInterfaces();
			this.interfaces = interfaces;
		}
		return interfaces;
	}

	/**
	 * <p>resolveInterfaces.</p>
	 *
	 * @return an array of {@link JavaType} objects
	 */
	protected JavaType[] resolveInterfaces() {
		Type[] interfaces = kind.getInterfaces(type);
		return from(interfaces, this);
	}

	/**
	 * <p>getInterface.</p>
	 *
	 * @param interfaceIndex a int
	 * @return a {@link JavaType} object
	 * @throws IndexOutOfBoundsException if any.
	 */
	public JavaType getInterface(int interfaceIndex) throws IndexOutOfBoundsException {
		return getInterfaces()[interfaceIndex];
	}

	/**
	 * <p>Getter for the field <code>genericTypes</code>.</p>
	 *
	 * @return an array of {@link JavaType} objects
	 */
	public JavaType[] getGenericTypes() {
		JavaType[] genericTypes = this.genericTypes;
		if (genericTypes == null) {
			genericTypes = resolveGenericTypes();
			this.genericTypes = genericTypes;
		}
		return genericTypes;
	}

	/**
	 * <p>resolveGenericTypes.</p>
	 *
	 * @return an array of {@link JavaType} objects
	 */
	protected JavaType[] resolveGenericTypes() {
		if (!OBJECT_TYPE.equals(this)) {
			Kind kind = this.kind;
			Type[] genericTypes = kind.getGenericTypes(this);
			return from(genericTypes, this);
		}
		return EMPTY_ARRAY;
	}

	/**
	 * <p>getGenericType.</p>
	 *
	 * @param genericTypeIndex a int
	 * @return a {@link JavaType} object
	 * @throws IndexOutOfBoundsException if any.
	 */
	public JavaType getGenericType(int genericTypeIndex) throws IndexOutOfBoundsException {
		return getGenericTypes()[genericTypeIndex];
	}

	/**
	 * Get the JavaType presenting the target class
	 *
	 * @param targetClass the target class
	 * @return <code>null</code> if can't cast to the target class
	 */
	@Nullable
	public JavaType as(Class<?> targetClass) {
		Type typeToMatch = this.type;
		Type targetType = searchSuperType(targetClass, typeToMatch);
		if (targetType == null) {
			targetType = searchInterfaceType(targetClass, typeToMatch);
		}
		return targetType == null ? null : from(targetType, this);
	}

	/**
	 * <p>toClass.</p>
	 *
	 * @param <T> a T class
	 * @return a {@link Class} object
	 */
	@Nullable
	public <T> Class<T> toClass() {
		Type rawType = getRawType();
		return rawType == null ? null : (Class<T>) rawType;
	}

	/**
	 * <p>toParameterizedType.</p>
	 *
	 * @return a {@link ParameterizedType} object
	 */
	@Nullable
	public ParameterizedType toParameterizedType() {
		return asParameterizedType(this.type);
	}

	/**
	 * <p>toTypeVariable.</p>
	 *
	 * @return a {@link TypeVariable} object
	 */
	@Nullable
	public TypeVariable toTypeVariable() {
		return TypeUtils.asTypeVariable(this.type);
	}

	/**
	 * <p>toWildcardType.</p>
	 *
	 * @return a {@link WildcardType} object
	 */
	@Nullable
	public WildcardType toWildcardType() {
		return TypeUtils.asWildcardType(this.type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "JavaType : " + type.getTypeName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JavaType javaType = (JavaType) o;
		return Objects.equals(type, javaType.type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{type});
	}

	private Type searchSuperType(Class<?> targetClass, Type typeToMatch) {
		Type targetType = null;
		Type superType = kind.getSuperType(typeToMatch);
		do {
			if (matches(targetClass, superType)) {
				// super type matched
				targetType = superType;
				break;
			}

			// To search interface type from super type
			targetType = searchInterfaceType(targetClass, superType);

			if (targetType == null) {
				superType = this.kind.getSuperType(superType);
			} else {
				break;
			}

		} while (!matches(Object.class, superType));

		return targetType;
	}

	private Type searchInterfaceType(Class<?> targetClass, Type typeToMatch) {
		Type targetType = null;
		Type[] interfaces = this.kind.getInterfaces(typeToMatch);
		for (Type interfaceType : interfaces) {
			if (matches(targetClass, interfaceType)) {
				targetType = interfaceType;
				break;
			} else {
				targetType = searchInterfaceType(targetClass, interfaceType);
				if (targetType != null) {
					break;
				}
			}
		}
		return targetType;
	}


	public enum Kind {

		/**
		 * The type kind presents Java {@link Class}
		 */
		CLASS(Class.class) {
			@Override
			public Type getSuperType(Type type) {
				Class klass = as(type);
				return klass.getGenericSuperclass();
			}

			@Override
			public Type getRawType(Type type) {
				return type;
			}

			@Override
			public Type[] getInterfaces(Type type) {
				Class klass = as(type);
				return klass.getGenericInterfaces();
			}

			@Override
			public Type[] getGenericTypes(JavaType javaType) {
				Class klass = as(javaType.type);
				TypeVariable<Class>[] typeParameters = klass.getTypeParameters();
				int length = typeParameters.length;
				return length == 0 ? super.getGenericTypes(javaType) : new Type[length];
			}

			private Class<?> as(Type type) {
				return TypeUtils.getRawClass(type);
			}
		},

		/**
		 * The type kind presents Java {@link ParameterizedType}
		 */
		PARAMETERIZED_TYPE(ParameterizedType.class) {
			@Override
			public Type getSuperType(Type type) {
				Type rawType = getRawType(type);
				Kind rawTypeKind = valueOf(rawType);
				return rawTypeKind.getSuperType(rawType);
			}

			@Override
			public Type getRawType(Type type) {
				ParameterizedType pType = as(type);
				Type rawType = pType.getRawType();
				return rawType;
			}

			@Override
			public Type[] getInterfaces(Type type) {
				Type rawType = getRawType(type);
				Kind rawTypeKind = valueOf(rawType);
				return rawTypeKind.getInterfaces(rawType);
			}

			@Override
			public Type[] getGenericTypes(JavaType javaType) {
				Type type = resolveType(javaType);
				Type baseType = javaType.type;
				final Type[] genericTypes;
				List<Type> genericTypesList = TypeUtils.resolveActualTypeArguments(type, baseType);
				if (genericTypesList.isEmpty()) {
					genericTypes = as(baseType).getActualTypeArguments();
					toActualTypes(genericTypes);
				} else {
					genericTypes = genericTypesList.toArray(new Type[]{});
				}
				return genericTypes;
			}

			private void toActualTypes(Type[] genericTypes) {
				int length = genericTypes.length;
				for (int i = 0; i < length; i++) {
					Type genericType = genericTypes[i];
					if (!TypeUtils.isActualType(genericType)) {
						// set null if type is not an actual type
						genericTypes[i] = null;
					}
				}
			}

			private Type resolveType(JavaType javaType) {
				JavaType source = javaType.getRootSource();
				if (source == null) {
					source = javaType.getSource();
				}
				return source == null ? javaType.type : source.type;
			}

			private ParameterizedType as(Type type) {
				return (ParameterizedType) type;
			}
		},

		/**
		 * The type kind presents Java {@link TypeVariable}
		 */
		TYPE_VARIABLE(TypeVariable.class),

		/**
		 * The type kind presents Java {@link WildcardType}
		 */
		WILDCARD_TYPE(WildcardType.class),

		/**
		 * The type kind presents Java {@link GenericArrayType}
		 * TODO
		 */
		GENERIC_ARRAY_TYPE(GenericArrayType.class),

		// TODO More Types

		UNKNOWN(Type.class);

		private final Class<? extends Type> typeClass;

		Kind(Class<? extends Type> typeClass) {
			this.typeClass = typeClass;
		}

		public static Kind valueOf(Type type) {
			Kind kind = Kind.UNKNOWN;
			for (Kind e : values()) {
				if (e.typeClass.isInstance(type)) {
					kind = e;
					break;
				}
			}
			return kind;
		}

		/**
		 * Get the raw type from the specified {@link Type type}
		 *
		 * @param type the specified {@link Type type}
		 * @return <code>null</code> as default
		 */
		public Type getRawType(Type type) {
			return null;
		}

		/**
		 * Get the super type from the specified {@link Type type}
		 *
		 * @param type the specified {@link Type type}
		 * @return <code>null</code> as default
		 */
		public Type getSuperType(Type type) {
			return null;
		}

		public Type[] getInterfaces(Type type) {
			return EMPTY_TYPE;
		}

		public Type[] getGenericTypes(JavaType javaType) {
			return EMPTY_TYPE;
		}

	}

}
