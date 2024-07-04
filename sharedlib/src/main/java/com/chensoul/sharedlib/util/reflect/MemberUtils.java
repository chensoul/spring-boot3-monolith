package com.chensoul.sharedlib.util.reflect;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

/**
 * Java Reflection {@link Member} Utilities class
 *
 * @author chensoul
 * @since 0.0.1
 */
public abstract class MemberUtils {

	/**
	 * Constant <code>STATIC_METHOD_PREDICATE</code>
	 */
	public final static Predicate<Method> STATIC_METHOD_PREDICATE = MemberUtils::isStatic;

	/**
	 * Constant <code>NON_STATIC_METHOD_PREDICATE</code>
	 */
	public final static Predicate<Member> NON_STATIC_METHOD_PREDICATE = MemberUtils::isNonStatic;

	/**
	 * Constant <code>FINAL_METHOD_PREDICATE</code>
	 */
	public final static Predicate<Member> FINAL_METHOD_PREDICATE = MemberUtils::isFinal;

	/**
	 * Constant <code>PUBLIC_METHOD_PREDICATE</code>
	 */
	public final static Predicate<Member> PUBLIC_METHOD_PREDICATE = MemberUtils::isPublic;

	/**
	 * Constant <code>NON_PRIVATE_METHOD_PREDICATE</code>
	 */
	public final static Predicate<Member> NON_PRIVATE_METHOD_PREDICATE = MemberUtils::isNonPrivate;

	private MemberUtils() {
	}

	/**
	 * check the specified {@link Member member} is static or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is static one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isStatic(Member member) {
		return member != null && Modifier.isStatic(member.getModifiers());
	}

	/**
	 * check the specified {@link Member member} is abstract or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is static one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isAbstract(Member member) {
		return member != null && Modifier.isAbstract(member.getModifiers());
	}

	/**
	 * <p>isNonStatic.</p>
	 *
	 * @param member a {@link Member} object
	 * @return a boolean
	 */
	public static boolean isNonStatic(Member member) {
		return member != null && !Modifier.isStatic(member.getModifiers());
	}

	/**
	 * check the specified {@link Member member} is final or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is final one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isFinal(Member member) {
		return member != null && Modifier.isFinal(member.getModifiers());
	}

	/**
	 * check the specified {@link Member member} is private or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is private one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isPrivate(Member member) {
		return member != null && Modifier.isPrivate(member.getModifiers());
	}

	/**
	 * check the specified {@link Member member} is public or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is public one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isPublic(Member member) {
		return member != null && Modifier.isPublic(member.getModifiers());
	}

	/**
	 * check the specified {@link Member member} is non-private or not ?
	 *
	 * @param member {@link Member} instance, e.g, {@link java.lang.reflect.Constructor}, {@link Method} or {@link java.lang.reflect.Field}
	 * @return Iff <code>member</code> is non-private one, return <code>true</code>, or <code>false</code>
	 */
	public static boolean isNonPrivate(Member member) {
		return member != null && !Modifier.isPrivate(member.getModifiers());
	}
}
