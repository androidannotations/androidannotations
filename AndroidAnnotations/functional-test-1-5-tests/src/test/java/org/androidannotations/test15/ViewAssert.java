/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.test15;

import static org.fest.assertions.Formatting.inBrackets;
import static org.fest.util.Strings.concat;

import org.fest.assertions.GenericAssert;

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
