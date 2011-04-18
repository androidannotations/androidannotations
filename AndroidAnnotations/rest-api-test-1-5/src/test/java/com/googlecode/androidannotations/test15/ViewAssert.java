package com.googlecode.androidannotations.test15;

import org.fest.assertions.GenericAssert;
import static org.fest.assertions.Formatting.inBrackets;
import static org.fest.util.Strings.concat;

import android.view.View;

public class ViewAssert extends GenericAssert<ViewAssert, View> {

	protected ViewAssert(View actual) {
		super(ViewAssert.class, actual);
	}

	public ViewAssert hasId(int id) {
		isNotNull();

		if (actual.getId() == id) {
			return this;
		}

		failIfCustomMessageIsSet();
		throw failure(concat("view id is ", inBrackets(actual.getId()), ", should be ", inBrackets(id)));
	}

}
