/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.helper;

/**
 * I'd love to use Guava's Optional, but we're trying to keep the dependency
 * level to a minimum.
 */
public final class Option<T> {

	private static final Option<?> ABSENT = new Option<>(null, false);

	public static <T> Option<T> of(T value) {
		return new Option<>(value, true);
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
