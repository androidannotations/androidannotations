package com.googlecode.androidannotations.api;

public enum Scope {

	/**
	 * A new instance of the bean is created each time it is needed
	 */
	Default, //

	/**
	 * A new instance of the bean is created the first time it is needed, it is
	 * then retained and the same instance is always returned.
	 */
	Singleton, //
	;
}
