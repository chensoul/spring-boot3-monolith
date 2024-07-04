package com.chensoul.sharedlib.util.lang.function;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

/**
 * A factory class for methods that wrap functional interfaces like
 * {@link Supplier} in a "blocking" ({@link ManagedBlocker}) equivalent, which
 * can be used with the {@link ForkJoinPool}.
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
public abstract class Blocking {

	private Blocking() {
	}

	/**
	 * <p>runnable.</p>
	 *
	 * @param runnable a {@link Runnable} object
	 * @return a {@link Runnable} object
	 */
	public static Runnable runnable(final Runnable runnable) {
		return () -> supplier(() -> {
			runnable.run();
			return null;
		}).get();
	}

	/**
	 * <p>biConsumer.</p>
	 *
	 * @param biConsumer a {@link BiConsumer} object
	 * @param <T>        a T class
	 * @param <U>        a U class
	 * @return a {@link BiConsumer} object
	 */
	public static <T, U> BiConsumer<T, U> biConsumer(final BiConsumer<? super T, ? super U> biConsumer) {
		return (t, u) -> runnable(() -> biConsumer.accept(t, u)).run();
	}

	/**
	 * <p>biFunction.</p>
	 *
	 * @param biFunction a {@link BiFunction} object
	 * @param <T>        a T class
	 * @param <U>        a U class
	 * @param <R>        a R class
	 * @return a {@link BiFunction} object
	 */
	public static <T, U, R> BiFunction<T, U, R> biFunction(final BiFunction<? super T, ? super U, ? extends R> biFunction) {
		return (t, u) -> supplier(() -> biFunction.apply(t, u)).get();
	}

	/**
	 * <p>biPredicate.</p>
	 *
	 * @param biPredicate a {@link BiPredicate} object
	 * @param <T>         a T class
	 * @param <U>         a U class
	 * @return a {@link BiPredicate} object
	 */
	public static <T, U> BiPredicate<T, U> biPredicate(final BiPredicate<? super T, ? super U> biPredicate) {
		return (t, u) -> supplier(() -> biPredicate.test(t, u)).get();
	}

	/**
	 * <p>binaryOperator.</p>
	 *
	 * @param binaryOperator a {@link BinaryOperator} object
	 * @param <T>            a T class
	 * @return a {@link BinaryOperator} object
	 */
	public static <T> BinaryOperator<T> binaryOperator(final BinaryOperator<T> binaryOperator) {
		return (t1, t2) -> supplier(() -> binaryOperator.apply(t1, t2)).get();
	}

	/**
	 * <p>booleanSupplier.</p>
	 *
	 * @param booleanSupplier a {@link BooleanSupplier} object
	 * @return a {@link BooleanSupplier} object
	 */
	public static BooleanSupplier booleanSupplier(final BooleanSupplier booleanSupplier) {
		return () -> supplier(booleanSupplier::getAsBoolean).get();
	}

	/**
	 * <p>consumer.</p>
	 *
	 * @param consumer a {@link Consumer} object
	 * @param <T>      a T class
	 * @return a {@link Consumer} object
	 */
	public static <T> Consumer<T> consumer(final Consumer<? super T> consumer) {
		return t -> runnable(() -> consumer.accept(t)).run();
	}

	/**
	 * <p>doubleBinaryOperator.</p>
	 *
	 * @param doubleBinaryOperator a {@link DoubleBinaryOperator} object
	 * @return a {@link DoubleBinaryOperator} object
	 */
	public static DoubleBinaryOperator doubleBinaryOperator(final DoubleBinaryOperator doubleBinaryOperator) {
		return (d1, d2) -> supplier(() -> doubleBinaryOperator.applyAsDouble(d1, d2)).get();
	}

	/**
	 * <p>doubleConsumer.</p>
	 *
	 * @param doubleConsumer a {@link DoubleConsumer} object
	 * @return a {@link DoubleConsumer} object
	 */
	public static DoubleConsumer doubleConsumer(final DoubleConsumer doubleConsumer) {
		return d -> runnable(() -> doubleConsumer.accept(d)).run();
	}

	/**
	 * <p>doubleFunction.</p>
	 *
	 * @param doubleFunction a {@link DoubleFunction} object
	 * @param <R>            a R class
	 * @return a {@link DoubleFunction} object
	 */
	public static <R> DoubleFunction<R> doubleFunction(final DoubleFunction<? extends R> doubleFunction) {
		return d -> supplier(() -> doubleFunction.apply(d)).get();
	}

	/**
	 * <p>doublePredicate.</p>
	 *
	 * @param doublePredicate a {@link DoublePredicate} object
	 * @return a {@link DoublePredicate} object
	 */
	public static DoublePredicate doublePredicate(final DoublePredicate doublePredicate) {
		return d -> supplier(() -> doublePredicate.test(d)).get();
	}

	/**
	 * <p>doubleSupplier.</p>
	 *
	 * @param doubleSupplier a {@link DoubleSupplier} object
	 * @return a {@link DoubleSupplier} object
	 */
	public static DoubleSupplier doubleSupplier(final DoubleSupplier doubleSupplier) {
		return () -> supplier(doubleSupplier::getAsDouble).get();
	}

	/**
	 * <p>doubleToIntFunction.</p>
	 *
	 * @param doubleToIntFunction a {@link DoubleToIntFunction} object
	 * @return a {@link DoubleToIntFunction} object
	 */
	public static DoubleToIntFunction doubleToIntFunction(final DoubleToIntFunction doubleToIntFunction) {
		return d -> supplier(() -> doubleToIntFunction.applyAsInt(d)).get();
	}

	/**
	 * <p>doubleToLongFunction.</p>
	 *
	 * @param doubleToLongFunction a {@link DoubleToLongFunction} object
	 * @return a {@link DoubleToLongFunction} object
	 */
	public static DoubleToLongFunction doubleToLongFunction(final DoubleToLongFunction doubleToLongFunction) {
		return d -> supplier(() -> doubleToLongFunction.applyAsLong(d)).get();
	}

	/**
	 * <p>doubleUnaryOperator.</p>
	 *
	 * @param doubleUnaryOperator a {@link DoubleUnaryOperator} object
	 * @return a {@link DoubleUnaryOperator} object
	 */
	public static DoubleUnaryOperator doubleUnaryOperator(final DoubleUnaryOperator doubleUnaryOperator) {
		return d -> supplier(() -> doubleUnaryOperator.applyAsDouble(d)).get();
	}

	/**
	 * <p>function.</p>
	 *
	 * @param function a {@link Function} object
	 * @param <T>      a T class
	 * @param <R>      a R class
	 * @return a {@link Function} object
	 */
	public static <T, R> Function<T, R> function(final Function<? super T, ? extends R> function) {
		return t -> supplier(() -> function.apply(t)).get();
	}

	/**
	 * <p>intBinaryOperator.</p>
	 *
	 * @param intBinaryOperator a {@link IntBinaryOperator} object
	 * @return a {@link IntBinaryOperator} object
	 */
	public static IntBinaryOperator intBinaryOperator(final IntBinaryOperator intBinaryOperator) {
		return (i1, i2) -> supplier(() -> intBinaryOperator.applyAsInt(i1, i2)).get();
	}

	/**
	 * <p>intConsumer.</p>
	 *
	 * @param intConsumer a {@link IntConsumer} object
	 * @return a {@link IntConsumer} object
	 */
	public static IntConsumer intConsumer(final IntConsumer intConsumer) {
		return i -> runnable(() -> intConsumer.accept(i)).run();
	}

	/**
	 * <p>intFunction.</p>
	 *
	 * @param intFunction a {@link IntFunction} object
	 * @param <R>         a R class
	 * @return a {@link IntFunction} object
	 */
	public static <R> IntFunction<R> intFunction(final IntFunction<? extends R> intFunction) {
		return i -> supplier(() -> intFunction.apply(i)).get();
	}

	/**
	 * <p>intPredicate.</p>
	 *
	 * @param intPredicate a {@link IntPredicate} object
	 * @return a {@link IntPredicate} object
	 */
	public static IntPredicate intPredicate(final IntPredicate intPredicate) {
		return i -> supplier(() -> intPredicate.test(i)).get();
	}

	/**
	 * <p>intSupplier.</p>
	 *
	 * @param intSupplier a {@link IntSupplier} object
	 * @return a {@link IntSupplier} object
	 */
	public static IntSupplier intSupplier(final IntSupplier intSupplier) {
		return () -> supplier(intSupplier::getAsInt).get();
	}

	/**
	 * <p>intToDoubleFunction.</p>
	 *
	 * @param intToDoubleFunction a {@link IntToDoubleFunction} object
	 * @return a {@link IntToDoubleFunction} object
	 */
	public static IntToDoubleFunction intToDoubleFunction(final IntToDoubleFunction intToDoubleFunction) {
		return i -> supplier(() -> intToDoubleFunction.applyAsDouble(i)).get();
	}

	/**
	 * <p>intToLongFunction.</p>
	 *
	 * @param intToLongFunction a {@link IntToLongFunction} object
	 * @return a {@link IntToLongFunction} object
	 */
	public static IntToLongFunction intToLongFunction(final IntToLongFunction intToLongFunction) {
		return i -> supplier(() -> intToLongFunction.applyAsLong(i)).get();
	}

	/**
	 * <p>intUnaryOperator.</p>
	 *
	 * @param intUnaryOperator a {@link IntUnaryOperator} object
	 * @return a {@link IntUnaryOperator} object
	 */
	public static IntUnaryOperator intUnaryOperator(final IntUnaryOperator intUnaryOperator) {
		return i -> supplier(() -> intUnaryOperator.applyAsInt(i)).get();
	}

	/**
	 * <p>longBinaryOperator.</p>
	 *
	 * @param longBinaryOperator a {@link LongBinaryOperator} object
	 * @return a {@link LongBinaryOperator} object
	 */
	public static LongBinaryOperator longBinaryOperator(final LongBinaryOperator longBinaryOperator) {
		return (l1, l2) -> supplier(() -> longBinaryOperator.applyAsLong(l1, l2)).get();
	}

	/**
	 * <p>longConsumer.</p>
	 *
	 * @param longConsumer a {@link LongConsumer} object
	 * @return a {@link LongConsumer} object
	 */
	public static LongConsumer longConsumer(final LongConsumer longConsumer) {
		return l -> runnable(() -> longConsumer.accept(l)).run();
	}

	/**
	 * <p>longFunction.</p>
	 *
	 * @param longFunction a {@link LongFunction} object
	 * @param <R>          a R class
	 * @return a {@link LongFunction} object
	 */
	public static <R> LongFunction<R> longFunction(final LongFunction<? extends R> longFunction) {
		return l -> supplier(() -> longFunction.apply(l)).get();
	}

	/**
	 * <p>longPredicate.</p>
	 *
	 * @param longPredicate a {@link LongPredicate} object
	 * @return a {@link LongPredicate} object
	 */
	public static LongPredicate longPredicate(final LongPredicate longPredicate) {
		return l -> supplier(() -> longPredicate.test(l)).get();
	}

	/**
	 * <p>longSupplier.</p>
	 *
	 * @param longSupplier a {@link LongSupplier} object
	 * @return a {@link LongSupplier} object
	 */
	public static LongSupplier longSupplier(final LongSupplier longSupplier) {
		return () -> supplier(longSupplier::getAsLong).get();
	}

	/**
	 * <p>longToDoubleFunction.</p>
	 *
	 * @param longToDoubleFunction a {@link LongToDoubleFunction} object
	 * @return a {@link LongToDoubleFunction} object
	 */
	public static LongToDoubleFunction longToDoubleFunction(final LongToDoubleFunction longToDoubleFunction) {
		return l -> supplier(() -> longToDoubleFunction.applyAsDouble(l)).get();
	}

	/**
	 * <p>longToIntFunction.</p>
	 *
	 * @param longToIntFunction a {@link LongToIntFunction} object
	 * @return a {@link LongToIntFunction} object
	 */
	public static LongToIntFunction longToIntFunction(final LongToIntFunction longToIntFunction) {
		return l -> supplier(() -> longToIntFunction.applyAsInt(l)).get();
	}

	/**
	 * <p>longUnaryOperator.</p>
	 *
	 * @param longUnaryOperator a {@link LongUnaryOperator} object
	 * @return a {@link LongUnaryOperator} object
	 */
	public static LongUnaryOperator longUnaryOperator(final LongUnaryOperator longUnaryOperator) {
		return l -> supplier(() -> longUnaryOperator.applyAsLong(l)).get();
	}

	/**
	 * <p>objDoubleConsumer.</p>
	 *
	 * @param objDoubleConsumer a {@link ObjDoubleConsumer} object
	 * @param <T>               a T class
	 * @return a {@link ObjDoubleConsumer} object
	 */
	public static <T> ObjDoubleConsumer<T> objDoubleConsumer(final ObjDoubleConsumer<T> objDoubleConsumer) {
		return (o, d) -> runnable(() -> objDoubleConsumer.accept(o, d)).run();
	}

	/**
	 * <p>objIntConsumer.</p>
	 *
	 * @param objIntConsumer a {@link ObjIntConsumer} object
	 * @param <T>            a T class
	 * @return a {@link ObjIntConsumer} object
	 */
	public static <T> ObjIntConsumer<T> objIntConsumer(final ObjIntConsumer<T> objIntConsumer) {
		return (o, i) -> runnable(() -> objIntConsumer.accept(o, i)).run();
	}

	/**
	 * <p>objLongConsumer.</p>
	 *
	 * @param objLongConsumer a {@link ObjLongConsumer} object
	 * @param <T>             a T class
	 * @return a {@link ObjLongConsumer} object
	 */
	public static <T> ObjLongConsumer<T> objLongConsumer(final ObjLongConsumer<T> objLongConsumer) {
		return (o, l) -> runnable(() -> objLongConsumer.accept(o, l)).run();
	}

	/**
	 * <p>predicate.</p>
	 *
	 * @param predicate a {@link Predicate} object
	 * @param <T>       a T class
	 * @return a {@link Predicate} object
	 */
	public static <T> Predicate<T> predicate(final Predicate<? super T> predicate) {
		return t -> supplier(() -> predicate.test(t)).get();
	}

	/**
	 * <p>supplier.</p>
	 *
	 * @param supplier a {@link Supplier} object
	 * @param <T>      a T class
	 * @return a {@link Supplier} object
	 */
	public static <T> Supplier<T> supplier(final Supplier<? extends T> supplier) {
		return new BlockingSupplier<>(supplier);
	}

	/**
	 * <p>toDoubleBiFunction.</p>
	 *
	 * @param toDoubleBiFunction a {@link ToDoubleBiFunction} object
	 * @param <T>                a T class
	 * @param <U>                a U class
	 * @return a {@link ToDoubleBiFunction} object
	 */
	public static <T, U> ToDoubleBiFunction<T, U> toDoubleBiFunction(final ToDoubleBiFunction<? super T, ? super U> toDoubleBiFunction) {
		return (t, u) -> supplier(() -> toDoubleBiFunction.applyAsDouble(t, u)).get();
	}

	/**
	 * <p>toDoubleFunction.</p>
	 *
	 * @param toDoubleFunction a {@link ToDoubleFunction} object
	 * @param <T>              a T class
	 * @return a {@link ToDoubleFunction} object
	 */
	public static <T> ToDoubleFunction<T> toDoubleFunction(final ToDoubleFunction<? super T> toDoubleFunction) {
		return t -> supplier(() -> toDoubleFunction.applyAsDouble(t)).get();
	}

	/**
	 * <p>toIntBiFunction.</p>
	 *
	 * @param toIntBiFunction a {@link ToIntBiFunction} object
	 * @param <T>             a T class
	 * @param <U>             a U class
	 * @return a {@link ToIntBiFunction} object
	 */
	public static <T, U> ToIntBiFunction<T, U> toIntBiFunction(final ToIntBiFunction<? super T, ? super U> toIntBiFunction) {
		return (t, u) -> supplier(() -> toIntBiFunction.applyAsInt(t, u)).get();
	}

	/**
	 * <p>toIntFunction.</p>
	 *
	 * @param toIntFunction a {@link ToIntFunction} object
	 * @param <T>           a T class
	 * @return a {@link ToIntFunction} object
	 */
	public static <T> ToIntFunction<T> toIntFunction(final ToIntFunction<? super T> toIntFunction) {
		return t -> supplier(() -> toIntFunction.applyAsInt(t)).get();
	}

	/**
	 * <p>toLongBiFunction.</p>
	 *
	 * @param toLongBiFunction a {@link ToLongBiFunction} object
	 * @param <T>              a T class
	 * @param <U>              a U class
	 * @return a {@link ToLongBiFunction} object
	 */
	public static <T, U> ToLongBiFunction<T, U> toLongBiFunction(final ToLongBiFunction<? super T, ? super U> toLongBiFunction) {
		return (t, u) -> supplier(() -> toLongBiFunction.applyAsLong(t, u)).get();
	}

	/**
	 * <p>toLongFunction.</p>
	 *
	 * @param toLongFunction a {@link ToLongFunction} object
	 * @param <T>            a T class
	 * @return a {@link ToLongFunction} object
	 */
	public static <T> ToLongFunction<T> toLongFunction(final ToLongFunction<? super T> toLongFunction) {
		return t -> supplier(() -> toLongFunction.applyAsLong(t)).get();
	}

	/**
	 * <p>unaryOperator.</p>
	 *
	 * @param unaryOperator a {@link UnaryOperator} object
	 * @param <T>           a T class
	 * @return a {@link UnaryOperator} object
	 */
	public static <T> UnaryOperator<T> unaryOperator(final UnaryOperator<T> unaryOperator) {
		return t -> supplier(() -> unaryOperator.apply(t)).get();
	}

	static class BlockingSupplier<T> implements Supplier<T> {
		private static final Object NULL = new Object();
		final Supplier<? extends T> supplier;
		volatile T result = (T) NULL;

		BlockingSupplier(final Supplier<? extends T> supplier) {
			this.supplier = supplier;
		}

		@Override
		public T get() {
			try {
				ForkJoinPool.managedBlock(new ManagedBlocker() {
					@Override
					public boolean block() throws InterruptedException {
						result = supplier.get();
						return true;
					}

					@Override
					public boolean isReleasable() {
						return result != NULL;
					}
				});
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}

			return result;
		}
	}
}
