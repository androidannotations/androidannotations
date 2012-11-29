package org.androidannotations.helper;

/**
 * I'd love to use Guava's Optional, but we're trying to keep the dependency
 * level to a minimum.
 */
public class Option<T> {

	private static final Option<?> ABSENT = new Option<Object>(null, false);

	public static <T> Option<T> of(T value) {
		return new Option<T>(value, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> Option<T> absent() {
		return (Option<T>) ABSENT;
	}

	private final T reference;

	private final boolean isPresent;

	private Option(T reference, boolean isPresent) {
		this.reference = reference;
		this.isPresent = isPresent;
	}

	public boolean isPresent() {
		return isPresent;
	}

	public boolean isAbsent() {
		return !isPresent;
	}

	public T get() {
		if (!isPresent) {
			throw new IllegalStateException("value is absent");
		}
		return reference;
	}

	public T getOr(T defaultValue) {
		if (isPresent) {
			return reference;
		} else {
			return defaultValue;
		}
	}

}
