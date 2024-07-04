package com.chensoul.sharedlib.util.reflect;

/**
 * The enumeration class for Java Reflection {@link java.lang.reflect.Modifier}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see java.lang.reflect.Modifier
 * @since 1.0.0
 */
public enum Modifier {

	/**
	 * The {@code public} modifier.
	 */
	PUBLIC(java.lang.reflect.Modifier.PUBLIC),

	/**
	 * The {@code private} modifier.
	 */
	PRIVATE(java.lang.reflect.Modifier.PRIVATE),

	/**
	 * The {@code protected} modifier.
	 */
	PROTECTED(java.lang.reflect.Modifier.PROTECTED),

	/**
	 * The {@code static} modifier.
	 */
	STATIC(java.lang.reflect.Modifier.STATIC),

	/**
	 * The {@code final} modifier.
	 */
	FINAL(java.lang.reflect.Modifier.FINAL),

	/**
	 * The {@code synchronized} modifier.
	 */
	SYNCHRONIZED(java.lang.reflect.Modifier.SYNCHRONIZED),

	/**
	 * The {@code volatile} modifier.
	 */
	VOLATILE(java.lang.reflect.Modifier.VOLATILE),

	/**
	 * The {@code transient} modifier.
	 */
	TRANSIENT(java.lang.reflect.Modifier.TRANSIENT),

	/**
	 * The {@code native} modifier.
	 */
	NATIVE(java.lang.reflect.Modifier.NATIVE),

	/**
	 * The {@code interface} modifier.
	 */
	INTERFACE(java.lang.reflect.Modifier.INTERFACE),

	/**
	 * The {@code abstract} modifier.
	 */
	ABSTRACT(java.lang.reflect.Modifier.ABSTRACT),

	/**
	 * The {@code strictfp} modifier.
	 */
	STRICT(java.lang.reflect.Modifier.STRICT),

	BRIDGE(0x00000040),

	VARARGS(0x00000080),

	SYNTHETIC(0x00001000),

	ANNOTATION(0x00002000),

	ENUM(0x00004000),

	MANDATED(0x00008000);

	private final int value;

	Modifier(int value) {
		this.value = value;
	}

	/**
	 * The bit value of modifier
	 *
	 * @return the bit value of modifier
	 */
	public int getValue() {
		return value;
	}

	/**
	 * matches the specified modifier
	 *
	 * @param mod the bit of modifier
	 * @return <code>true</code> if matches, otherwise <code>false</code>
	 */
	public boolean matches(int mod) {
		return (mod & value) != 0;
	}
}
