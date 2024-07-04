package com.chensoul.sharedlib.util.reflect;


import com.chensoul.sharedlib.util.lang.function.Predicates;
import static com.chensoul.sharedlib.util.lang.function.Predicates.EMPTY_PREDICATE_ARRAY;
import com.chensoul.sharedlib.util.lang.function.Streams;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import static java.lang.Integer.getInteger;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

/**
 * TODO
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public abstract class TypeUtils {
	public static final Predicate<Type> NON_OBJECT_TYPE_FILTER = t -> t != null && !isObjectType(t);

	/**
	 * Constant <code>NON_OBJECT_CLASS_FILTER</code>
	 */
	public static final Predicate<Class<?>> NON_OBJECT_CLASS_FILTER = (Predicate) NON_OBJECT_TYPE_FILTER;

	/**
	 * Constant <code>TYPE_VARIABLE_FILTER</code>
	 */
	public static final Predicate<Type> TYPE_VARIABLE_FILTER = TypeUtils::isTypeVariable;

	/**
	 * Constant <code>PARAMETERIZED_TYPE_FILTER</code>
	 */
	public static final Predicate<Type> PARAMETERIZED_TYPE_FILTER = TypeUtils::isParameterizedType;

	/**
	 * Constant <code>WILDCARD_TYPE_FILTER</code>
	 */
	public static final Predicate<Type> WILDCARD_TYPE_FILTER = TypeUtils::isWildcardType;

	/**
	 * Constant <code>GENERIC_ARRAY_TYPE_FILTER</code>
	 */
	public static final Predicate<Type> GENERIC_ARRAY_TYPE_FILTER = TypeUtils::isGenericArrayType;

	/**
	 * Empty {@link Type} array
	 */
	public static final Type[] EMPTY_TYPE = new Type[0];

	/**
	 * Constant <code>RESOLVED_GENERIC_TYPES_CACHE_SIZE_PROPERTY_NAME="microsphere.reflect.resolved-generic-ty"{trunked}</code>
	 */
	public static final String RESOLVED_GENERIC_TYPES_CACHE_SIZE_PROPERTY_NAME = "microsphere.reflect.resolved-generic-types.cache.size";

	private static final ConcurrentMap<MultipleType, List<Type>> resolvedGenericTypesCache = new ConcurrentHashMap<>(getInteger(RESOLVED_GENERIC_TYPES_CACHE_SIZE_PROPERTY_NAME, 256));

	/**
	 * <p>isClass.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isClass(Object type) {
		return type instanceof Class;
	}

	/**
	 * <p>isObjectType.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isObjectType(Object type) {
		return Object.class.equals(type);
	}

	/**
	 * <p>isParameterizedType.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isParameterizedType(Object type) {
		return type instanceof ParameterizedType;
	}

	/**
	 * <p>isTypeVariable.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isTypeVariable(Object type) {
		return type instanceof TypeVariable;
	}

	/**
	 * <p>isWildcardType.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isWildcardType(Object type) {
		return type instanceof WildcardType;
	}

	/**
	 * <p>isGenericArrayType.</p>
	 *
	 * @param type a {@link Object} object
	 * @return a boolean
	 */
	public static boolean isGenericArrayType(Object type) {
		return type instanceof GenericArrayType;
	}

	/**
	 * <p>getRawType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link Type} object
	 */
	public static Type getRawType(Type type) {
		if (isParameterizedType(type)) {
			return ((ParameterizedType) type).getRawType();
		} else {
			return type;
		}
	}

	/**
	 * <p>getRawClass.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link Class} object
	 */
	public static Class<?> getRawClass(Type type) {
		Type rawType = getRawType(type);
		if (isClass(rawType)) {
			return (Class) rawType;
		}
		return null;
	}

	/**
	 * the semantics is same as {@link Class#isAssignableFrom(Class)}
	 *
	 * @param superType  the super type
	 * @param targetType the target type
	 * @return see {@link Class#isAssignableFrom(Class)}
	 */
	public static boolean isAssignableFrom(Type superType, Type targetType) {
		Class<?> superClass = asClass(superType);
		return isAssignableFrom(superClass, targetType);
	}

	/**
	 * the semantics is same as {@link Class#isAssignableFrom(Class)}
	 *
	 * @param superClass the super class
	 * @param targetType the target type
	 * @return see {@link Class#isAssignableFrom(Class)}
	 */
	public static boolean isAssignableFrom(Class<?> superClass, Type targetType) {
		Class<?> targetClass = asClass(targetType);
		return isAssignableFrom(superClass, targetClass);
	}

	/**
	 * the semantics is same as {@link Class#isAssignableFrom(Class)}
	 *
	 * @param superType  the super type
	 * @param targetType the target type
	 * @return see {@link Class#isAssignableFrom(Class)}
	 */
	protected static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
		return ClassUtils.isAssignableFrom(superType, targetType);
	}

	/**
	 * Resolve the actual type parameters from the specified type based on some type
	 *
	 * @param type     the type to be resolved
	 * @param baseType The base class or interface of <code>type</code>
	 * @return the actual type parameters
	 */
	public static List<Type> resolveActualTypeArguments(Type type, Type baseType) {
		return unmodifiableList(doResolveActualTypeArguments(type, baseType));
	}

	/**
	 * Resolve the actual type parameters from the specified type based on some type
	 *
	 * @param type     the type to be resolved
	 * @param baseType The base class or interface of <code>type</code>
	 * @param index    a int
	 * @return the actual type parameters
	 */
	public static Type resolveActualTypeArgument(Type type, Type baseType, int index) {
		return doResolveActualTypeArguments(type, baseType).get(index);
	}

	/**
	 * Resolve the classes of actual type parameters from the specified type based on some type
	 *
	 * @param type     the type to be resolved
	 * @param baseType The base class or interface of <code>type</code>
	 * @return the read-only {@link List} classes of the actual type parameters
	 */
	public static List<Class> resolveActualTypeArgumentClasses(Type type, Type baseType) {
		return unmodifiableList(doResolveActualTypeArgumentClasses(type, baseType));
	}

	/**
	 * Resolve the class of actual type parameter from the specified type and index based on some type
	 *
	 * @param type     the type to be resolved
	 * @param baseType The base class or interface of <code>type</code>
	 * @param index    the index of actual type parameter
	 * @return the read-only {@link List} classes of the actual type parameters
	 */
	public static Class resolveActualTypeArgumentClass(Type type, Class baseType, int index) {
		return asClass(resolveActualTypeArgument(type, baseType, index));
	}

	/**
	 * <p>doResolveActualTypeArgumentClasses.</p>
	 *
	 * @param type     a {@link Type} object
	 * @param baseType a {@link Type} object
	 * @return a {@link List} object
	 */
	protected static List<Class> doResolveActualTypeArgumentClasses(Type type, Type baseType) {
		return doResolveActualTypeArguments(type, baseType).stream().map(TypeUtils::asClass).filter(Objects::nonNull).collect(toList());
	}

	/**
	 * <p>doResolveActualTypeArguments.</p>
	 *
	 * @param type     a {@link Type} object
	 * @param baseType a {@link Type} object
	 * @return a {@link List} object
	 */
	protected static List<Type> doResolveActualTypeArguments(Type type, Type baseType) {
		Class baseClass = asClass(baseType);
		return doResolveActualTypeArguments(type, baseClass);
	}

	/**
	 * <p>resolveActualTypeArguments.</p>
	 *
	 * @param type      a {@link Type} object
	 * @param baseClass a {@link Class} object
	 * @return a {@link List} object
	 */
	public static List<Type> resolveActualTypeArguments(Type type, Class baseClass) {
		return unmodifiableList(doResolveActualTypeArguments(type, baseClass));
	}

	/**
	 * <p>doResolveActualTypeArguments.</p>
	 *
	 * @param type      a {@link Type} object
	 * @param baseClass a {@link Class} object
	 * @return a {@link List} object
	 */
	protected static List<Type> doResolveActualTypeArguments(Type type, Class baseClass) {
		return resolvedGenericTypesCache.computeIfAbsent(MultipleType.of(type, baseClass), mt -> {

			if (type == null) { // the raw class of type
				return emptyList();
			}

			if (baseClass == null) { // baseType are null
				return emptyList();
			}

			TypeVariable<Class>[] baseTypeParameters = baseClass.getTypeParameters();
			int baseTypeParametersLength = baseTypeParameters.length;
			if (baseTypeParametersLength == 0) { // No type-parameter in the base class
				return emptyList();
			}

			Class klass = asClass(type);

			boolean sameClass = baseClass.equals(klass);

			if (sameClass) { // Fast Path
				return doResolveActualTypeArgumentsInFastPath(type);
			} else if (!baseClass.isAssignableFrom(klass)) {
				// No hierarchical relationship between type and baseType
				return emptyList();
			}

			Predicate<Type> baseClassFilter = t -> isAssignableFrom(baseClass, t);

			List<Type> hierarchicalTypes = doFindAllHierarchicalTypes(type, baseClassFilter);

			int hierarchicalTypesSize = hierarchicalTypes.size();

			Map<Class, TypeArgument[]> typeArgumentsMap = resolveTypeArgumentsMap(type, hierarchicalTypes, hierarchicalTypesSize, baseClass, baseTypeParameters);

			Type[] actualTypeArguments = new Type[baseTypeParametersLength];

			int actualTypeArgumentsCount = 0;

			for (TypeArgument[] typeArguments : typeArgumentsMap.values()) {
				for (int i = 0; i < baseTypeParametersLength; i++) {
					TypeArgument typeArgument = typeArguments[i];
					if (typeArgument != null) {
						Type actualTypeArgument = typeArgument.getType();
						actualTypeArguments[i] = actualTypeArgument;
						actualTypeArgumentsCount++;
					}
				}
				if (actualTypeArgumentsCount == baseTypeParametersLength) {
					break;
				}
			}

			return Arrays.asList(actualTypeArguments);
		});
	}

	private static List<Type> doResolveActualTypeArgumentsInFastPath(Type type) {
		ParameterizedType pType = asParameterizedType(type);
		if (pType != null) {
			Type[] actualTypeArguments = pType.getActualTypeArguments();
			int actualTypeArgumentsLength = actualTypeArguments.length;
			List<Type> actualTypeArgumentsList = Lists.newArrayList();
			for (int i = 0; i < actualTypeArgumentsLength; i++) {
				Type actualTypeArgument = actualTypeArguments[i];
				if (isActualType(actualTypeArgument)) {
					actualTypeArgumentsList.add(actualTypeArgument);
				}
			}
			return actualTypeArgumentsList;
		}
		return emptyList();
	}

	private static Map<Class, TypeArgument[]> resolveTypeArgumentsMap(Type type, List<Type> hierarchicalTypes, int hierarchicalTypesSize, Class baseClass, TypeVariable<Class>[] baseTypeParameters) {

		int size = hierarchicalTypesSize + 1;

		Map<Class, TypeArgument[]> typeArgumentsMap = Maps.newLinkedHashMapWithExpectedSize(size);

		for (int i = hierarchicalTypesSize - 1; i > -1; i--) {
			Type hierarchicalType = hierarchicalTypes.get(i);
			resolveTypeArgumentsMap(hierarchicalType, hierarchicalTypes, i, hierarchicalTypesSize, typeArgumentsMap, baseClass, baseTypeParameters);
		}

		resolveTypeArgumentsMap(type, hierarchicalTypes, -1, hierarchicalTypesSize, typeArgumentsMap, baseClass, baseTypeParameters);


		return typeArgumentsMap;
	}

	private static void resolveTypeArgumentsMap(Type type, List<Type> hierarchicalTypes, int index, int hierarchicalTypesSize, Map<Class, TypeArgument[]> typeArgumentsMap, Class baseClass, TypeVariable<Class>[] baseTypeParameters) {
		ParameterizedType pType = asParameterizedType(type);
		if (pType != null) {
			resolveTypeArgumentsMap(pType, hierarchicalTypes, index, hierarchicalTypesSize, typeArgumentsMap, baseClass, baseTypeParameters);
		}
	}

	private static void resolveTypeArgumentsMap(ParameterizedType type, List<Type> hierarchicalTypes, int index, int hierarchicalTypesSize, Map<Class, TypeArgument[]> typeArgumentsMap, Class baseClass, TypeVariable<Class>[] baseTypeParameters) {
		Class klass = asClass(type);

		int baseTypeArgumentsLength = baseTypeParameters.length;
		TypeArgument[] typeArguments = newTypeArguments(klass, typeArgumentsMap, baseTypeArgumentsLength);

		Type[] actualTypeArguments = type.getActualTypeArguments();
		int actualTypeArgumentsLength = actualTypeArguments.length;

		int length = Math.min(actualTypeArgumentsLength, baseTypeArgumentsLength);

		int actualTypesCount = 0;

		for (int i = 0; i < length; i++) {
			Type actualTypeArgument = actualTypeArguments[i];
			if (isActualType(actualTypeArgument)) {
				actualTypesCount++;
				typeArguments[i] = TypeArgument.create(actualTypeArgument, i);
			}
		}

		if (klass.equals(baseClass)) { // 'klass' is same as baseClass
			return;
		}

		if (actualTypesCount < baseTypeArgumentsLength) { // To find actual types from the hierarchical types of 'type'
			TypeVariable<Class>[] typeParameters = klass.getTypeParameters();
			int typeParametersLength = typeParameters.length;
			for (int superTypeIndex = index + 1; superTypeIndex < hierarchicalTypesSize; superTypeIndex++) {
				Type superType = hierarchicalTypes.get(superTypeIndex);
				Class superClass = asClass(superType);
				TypeVariable<Class>[] superTypeParameters = superClass.getTypeParameters();
				int superTypeParametersLength = superTypeParameters.length;
				TypeArgument[] superTypeArguments = getTypeArguments(superClass, typeArgumentsMap);
				if (superTypeArguments != null) {
					for (int typeParameterIndex = 0; typeParameterIndex < typeParametersLength; typeParameterIndex++) {
						TypeVariable<Class> typeParameter = typeParameters[typeParameterIndex];
						String typeParameterName = typeParameter.getName();
						for (int superTypeParameterIndex = 0; superTypeParameterIndex < superTypeParametersLength; superTypeParameterIndex++) {
							TypeVariable<Class> superTypeParameter = superTypeParameters[superTypeParameterIndex];
							String superTypeParameterName = superTypeParameter.getName();
							if (typeParameterName.equals(superTypeParameterName)) {
								superTypeArguments[superTypeParameterIndex] = typeArguments[typeParameterIndex];
							}
						}
					}
				}
			}
		}
	}

	private static TypeArgument[] newTypeArguments(Class klass, Map<Class, TypeArgument[]> typeArgumentsMap, int typeArgumentsLength) {
		return typeArgumentsMap.computeIfAbsent(klass, t -> new TypeArgument[typeArgumentsLength]);
	}

	private static TypeArgument[] getTypeArguments(Class klass, Map<Class, TypeArgument[]> typeArgumentsMap) {
		return typeArgumentsMap.get(klass);
	}

	/**
	 * <p>isActualType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a boolean
	 */
	public static boolean isActualType(Type type) {
		return isClass(type) || isParameterizedType(type);
	}

	/**
	 * Get the specified types' generic types(including super classes and interfaces) that are assignable from {@link ParameterizedType} interface
	 *
	 * @param type        the specified type
	 * @param typeFilters one or more {@link Predicate}s to support the {@link ParameterizedType} instance
	 * @return non-null read-only {@link List}
	 */
	public static List<ParameterizedType> getGenericTypes(Type type, Predicate<ParameterizedType>... typeFilters) {

		Class<?> rawClass = getRawClass(type);

		if (rawClass == null) {
			return emptyList();
		}

		List<Type> genericTypes = new LinkedList<>();

		genericTypes.add(rawClass.getGenericSuperclass());
		genericTypes.addAll(asList(rawClass.getGenericInterfaces()));

		return unmodifiableList(Streams.filterList(genericTypes, TypeUtils::isParameterizedType).stream().map(ParameterizedType.class::cast).filter(Predicates.and(typeFilters)).collect(toList()));
	}

	/**
	 * <p>findAllTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link List} object
	 */
	public static List<Type> findAllTypes(Type type, Predicate<Type>... typeFilters) {
		List<Type> allGenericTypes = Lists.newLinkedList();
		Predicate filter = Predicates.and(typeFilters);
		if (filter.test(type)) {
			// add self
			allGenericTypes.add(type);
		}
		// Add all hierarchical types in declaration order
		addAllHierarchicalTypes(allGenericTypes, type, filter);
		return unmodifiableList(allGenericTypes);

	}

	/**
	 * <p>findHierarchicalTypes.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link List} object
	 */
	public static List<Type> findHierarchicalTypes(Type type) {
		return findHierarchicalTypes(type, EMPTY_PREDICATE_ARRAY);
	}

	/**
	 * <p>findHierarchicalTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link List} object
	 */
	public static List<Type> findHierarchicalTypes(Type type, Predicate<Type>... typeFilters) {
		return unmodifiableList(doFindHierarchicalTypes(type, typeFilters));
	}

	/**
	 * <p>doFindHierarchicalTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link List} object
	 */
	protected static List<Type> doFindHierarchicalTypes(Type type, Predicate<Type>... typeFilters) {
		Class<?> klass = asClass(type);
		return doFindHierarchicalTypes(klass, typeFilters);
	}

	/**
	 * <p>doFindHierarchicalTypes.</p>
	 *
	 * @param klass       a {@link Class} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link List} object
	 */
	protected static List<Type> doFindHierarchicalTypes(Class<?> klass, Predicate<Type>... typeFilters) {
		if (klass == null || isObjectType(klass)) {
			return emptyList();
		}

		LinkedList<Type> types = Lists.newLinkedList();

		Predicate<? super Type> filter = Predicates.and(typeFilters);

		Type superType = klass.getGenericSuperclass();

		if (superType != null && filter.test(superType)) { // interface type will return null
			types.add(superType);
		}

		Type[] interfaceTypes = klass.getGenericInterfaces();
		for (Type interfaceType : interfaceTypes) {
			if (filter.test(interfaceType)) {
				types.add(interfaceType);
			}
		}
		return types;
	}


	/**
	 * <p>findAllHierarchicalTypes.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link List} object
	 */
	public static List<Type> findAllHierarchicalTypes(Type type) {
		return findAllHierarchicalTypes(type, EMPTY_PREDICATE_ARRAY);
	}

	/**
	 * <p>findAllHierarchicalTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link List} object
	 */
	public static List<Type> findAllHierarchicalTypes(Type type, Predicate<Type>... typeFilters) {
		return unmodifiableList(doFindAllHierarchicalTypes(type, typeFilters));
	}

	/**
	 * <p>doFindAllHierarchicalTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link LinkedList} object
	 */
	protected static LinkedList<Type> doFindAllHierarchicalTypes(Type type, Predicate<Type>... typeFilters) {
		LinkedList<Type> allTypes = Lists.newLinkedList();
		addAllHierarchicalTypes(allTypes, type, typeFilters);
		return allTypes;
	}

	/**
	 * <p>addAllHierarchicalTypes.</p>
	 *
	 * @param allTypes    a {@link List} object
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 */
	protected static void addAllHierarchicalTypes(List<Type> allTypes, Type type, Predicate<Type>... typeFilters) {

		List<Type> hierarchicalTypes = doFindHierarchicalTypes(type, typeFilters);

		int hierarchicalTypesSize = hierarchicalTypes.size();

		if (hierarchicalTypesSize < 1) {
			return;
		}

		allTypes.addAll(hierarchicalTypes);

		for (int i = 0; i < hierarchicalTypesSize; i++) {
			Type hierarchicalType = hierarchicalTypes.get(i);
			addAllHierarchicalTypes(allTypes, hierarchicalType, typeFilters);
		}
	}

	/**
	 * Get all generic types(including super classes and interfaces) that are assignable from {@link ParameterizedType} interface
	 *
	 * @param type        the specified type
	 * @param typeFilters one or more {@link Predicate}s to support the {@link ParameterizedType} instance
	 * @return non-null read-only {@link List}
	 */
	public static List<ParameterizedType> getAllGenericTypes(Type type, Predicate<ParameterizedType>... typeFilters) {
		List<ParameterizedType> allGenericTypes = new LinkedList<>();
		// Add generic super classes
		allGenericTypes.addAll(getAllGenericSuperClasses(type, typeFilters));
		// Add generic super interfaces
		allGenericTypes.addAll(getAllGenericInterfaces(type, typeFilters));
		// wrap unmodifiable object
		return unmodifiableList(allGenericTypes);
	}

	/**
	 * Get all generic super classes that are assignable from {@link ParameterizedType} interface
	 *
	 * @param type        the specified type
	 * @param typeFilters one or more {@link Predicate}s to support the {@link ParameterizedType} instance
	 * @return non-null read-only {@link List}
	 */
	public static List<ParameterizedType> getAllGenericSuperClasses(Type type, Predicate<ParameterizedType>... typeFilters) {

		Class<?> rawClass = getRawClass(type);

		if (rawClass == null || rawClass.isInterface()) {
			return emptyList();
		}

		List<Class<?>> allTypes = new LinkedList<>();
		// Add current class
		allTypes.add(rawClass);
		// Add all super classes
		allTypes.addAll(ClassUtils.getAllSuperClasses(rawClass, NON_OBJECT_CLASS_FILTER));

		List<ParameterizedType> allGenericSuperClasses = allTypes.stream().map(Class::getGenericSuperclass).filter(TypeUtils::isParameterizedType).map(ParameterizedType.class::cast).collect(Collectors.toList());

		return unmodifiableList(Streams.filterAll(allGenericSuperClasses, typeFilters));
	}

	/**
	 * Get all generic interfaces that are assignable from {@link ParameterizedType} interface
	 *
	 * @param type        the specified type
	 * @param typeFilters one or more {@link Predicate}s to support the {@link ParameterizedType} instance
	 * @return non-null read-only {@link List}
	 */
	public static List<ParameterizedType> getAllGenericInterfaces(Type type, Predicate<ParameterizedType>... typeFilters) {

		Class<?> rawClass = getRawClass(type);

		if (rawClass == null) {
			return emptyList();
		}

		List<Class<?>> allTypes = new LinkedList<>();
		// Add current class
		allTypes.add(rawClass);
		// Add all super classes
		allTypes.addAll(ClassUtils.getAllSuperClasses(rawClass, NON_OBJECT_CLASS_FILTER));
		// Add all super interfaces
		allTypes.addAll(ClassUtils.getAllInterfaces(rawClass));

		List<ParameterizedType> allGenericInterfaces = allTypes.stream().map(Class::getGenericInterfaces).map(Arrays::asList).flatMap(Collection::stream).map(TypeUtils::asParameterizedType).filter(Objects::nonNull).collect(toList());

		return unmodifiableList(Streams.filterAll(allGenericInterfaces, typeFilters));
	}

	/**
	 * <p>getClassName.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link String} object
	 */
	public static String getClassName(Type type) {
		return getRawType(type).getTypeName();
	}

	/**
	 * <p>getClassNames.</p>
	 *
	 * @param types a {@link Iterable} object
	 * @return a {@link Set} object
	 */
	public static Set<String> getClassNames(Iterable<? extends Type> types) {
		return stream(types.spliterator(), false).map(TypeUtils::getClassName).collect(toSet());
	}

	/**
	 * <p>resolveTypeArguments.</p>
	 *
	 * @param targetClass a {@link Class} object
	 * @return a {@link List} object
	 */
	public static List<Class<?>> resolveTypeArguments(Class<?> targetClass) {
		List<Class<?>> typeArguments = emptyList();
		while (targetClass != null) {
			typeArguments = resolveTypeArgumentsFromInterfaces(targetClass);
			if (!typeArguments.isEmpty()) {
				break;
			}

			Type superType = targetClass.getGenericSuperclass();
			if (superType instanceof ParameterizedType) {
				typeArguments = resolveTypeArgumentsFromType(superType);
			}

			if (!typeArguments.isEmpty()) {
				break;
			}
			// recursively
			targetClass = targetClass.getSuperclass();
		}

		return typeArguments;
	}

	/**
	 * <p>resolveTypeArgumentsFromInterfaces.</p>
	 *
	 * @param type a {@link Class} object
	 * @return a {@link List} object
	 */
	public static List<Class<?>> resolveTypeArgumentsFromInterfaces(Class<?> type) {
		List<Class<?>> typeArguments = emptyList();
		for (Type superInterface : type.getGenericInterfaces()) {
			typeArguments = resolveTypeArgumentsFromType(superInterface);
			if (typeArguments != null && !typeArguments.isEmpty()) {
				break;
			}
		}
		return typeArguments;
	}

	/**
	 * <p>resolveTypeArgumentsFromType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link List} object
	 */
	public static List<Class<?>> resolveTypeArgumentsFromType(Type type) {
		List<Class<?>> typeArguments = emptyList();
		if (type instanceof ParameterizedType) {
			typeArguments = new LinkedList<>();
			ParameterizedType pType = (ParameterizedType) type;
			if (pType.getRawType() instanceof Class) {
				for (Type argument : pType.getActualTypeArguments()) {
					Class<?> typeArgument = asClass(argument);
					if (typeArgument != null) {
						typeArguments.add(typeArgument);
					}
				}
			}
		}
		return typeArguments;
	}

	/**
	 * <p>asClass.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link Class} object
	 */
	public static Class<?> asClass(Type type) {
		Class targetClass = asClass0(type);
		if (targetClass == null) { // try to cast a ParameterizedType if possible
			ParameterizedType parameterizedType = asParameterizedType(type);
			if (parameterizedType != null) {
				targetClass = asClass(parameterizedType.getRawType());
			}
		}
		if (targetClass == null) { // try to cast a component type of GenericArrayType if possible
			GenericArrayType genericArrayType = asGenericArrayType(type);
			if (genericArrayType != null) {
				targetClass = asClass(genericArrayType.getGenericComponentType());
			}
		}
		return targetClass;
	}

	private static Class<?> asClass0(Type type) {
		return isClass(type) ? (Class<?>) type : null;
	}

	/**
	 * <p>asGenericArrayType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link GenericArrayType} object
	 */
	public static GenericArrayType asGenericArrayType(Type type) {
		if (type instanceof GenericArrayType) {
			return (GenericArrayType) type;
		}
		return null;
	}

	/**
	 * <p>asParameterizedType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link ParameterizedType} object
	 */
	public static ParameterizedType asParameterizedType(Type type) {
		if (isParameterizedType(type)) {
			return (ParameterizedType) type;
		}
		return null;
	}

	/**
	 * <p>asTypeVariable.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link TypeVariable} object
	 */
	public static TypeVariable asTypeVariable(Type type) {
		if (isTypeVariable(type)) {
			return (TypeVariable) type;
		}
		return null;
	}

	/**
	 * <p>asWildcardType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link WildcardType} object
	 */
	public static WildcardType asWildcardType(Type type) {
		if (isWildcardType(type)) {
			return (WildcardType) type;
		}
		return null;
	}

	/**
	 * <p>getComponentType.</p>
	 *
	 * @param type a {@link Type} object
	 * @return a {@link Type} object
	 */
	public static Type getComponentType(Type type) {
		GenericArrayType genericArrayType = asGenericArrayType(type);
		if (genericArrayType != null) {
			return genericArrayType.getGenericComponentType();
		} else {
			Class klass = asClass(type);
			return klass != null ? klass.getComponentType() : null;
		}
	}

	/**
	 * Get all super types from the specified type
	 *
	 * @param type        the specified type
	 * @param typeFilters the filters for type
	 * @return non-null read-only {@link Set}
	 * @since 0.0.1
	 */
	public static Set<Type> getAllSuperTypes(Type type, Predicate<Type>... typeFilters) {

		Class<?> rawClass = getRawClass(type);

		if (rawClass == null) {
			return emptySet();
		}

		if (rawClass.isInterface()) {
			return unmodifiableSet(Streams.filterAll(singleton(Object.class), typeFilters));
		}

		Set<Type> allSuperTypes = new LinkedHashSet<>();


		Type superType = rawClass.getGenericSuperclass();
		while (superType != null) {
			// add current super class
			allSuperTypes.add(superType);
			Class<?> superClass = getRawClass(superType);
			superType = superClass.getGenericSuperclass();
		}

		return Streams.filterAll(allSuperTypes, typeFilters);
	}

	/**
	 * Get all super interfaces from the specified type
	 *
	 * @param type        the specified type
	 * @param typeFilters the filters for type
	 * @return non-null read-only {@link Set}
	 * @since 0.0.1
	 */
	public static Set<Type> getAllInterfaces(Type type, Predicate<Type>... typeFilters) {

		Class<?> rawClass = getRawClass(type);

		if (rawClass == null) {
			return emptySet();
		}

		Set<Type> allSuperInterfaces = new LinkedHashSet<>();

		Type[] interfaces = rawClass.getGenericInterfaces();

		// find direct interfaces recursively
		for (Type interfaceType : interfaces) {
			allSuperInterfaces.add(interfaceType);
			allSuperInterfaces.addAll(getAllInterfaces(interfaceType, typeFilters));
		}

		// find super types recursively
		for (Type superType : getAllSuperTypes(type, typeFilters)) {
			allSuperInterfaces.addAll(getAllInterfaces(superType));
		}

		return Streams.filterAll(allSuperInterfaces, typeFilters);
	}

	/**
	 * <p>getAllTypes.</p>
	 *
	 * @param type        a {@link Type} object
	 * @param typeFilters a {@link Predicate} object
	 * @return a {@link Set} object
	 */
	public static Set<Type> getAllTypes(Type type, Predicate<Type>... typeFilters) {

		Set<Type> allTypes = new LinkedHashSet<>();

		// add the specified type
		allTypes.add(type);
		// add all super types
		allTypes.addAll(getAllSuperTypes(type));
		// add all super interfaces
		allTypes.addAll(getAllInterfaces(type));

		return Streams.filterAll(allTypes, typeFilters);
	}

	/**
	 * <p>findParameterizedTypes.</p>
	 *
	 * @param sourceClass a {@link Class} object
	 * @return a {@link Set} object
	 */
	public static Set<ParameterizedType> findParameterizedTypes(Class<?> sourceClass) {
		// Add Generic Interfaces
		List<Type> genericTypes = new LinkedList<>(asList(sourceClass.getGenericInterfaces()));
		// Add Generic Super Class
		genericTypes.add(sourceClass.getGenericSuperclass());

		Set<ParameterizedType> parameterizedTypes = genericTypes.stream().filter(type -> type instanceof ParameterizedType)// support ParameterizedType
			.map(ParameterizedType.class::cast)  // cast to ParameterizedType
			.collect(Collectors.toSet());

		if (parameterizedTypes.isEmpty()) { // If not found, try to search super types recursively
			genericTypes.stream().filter(type -> type instanceof Class).map(Class.class::cast).forEach(superClass -> parameterizedTypes.addAll(findParameterizedTypes(superClass)));
		}

		return unmodifiableSet(parameterizedTypes);                     // build as a Set

	}
}
