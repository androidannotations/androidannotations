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
package org.androidannotations.eviewgroup;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.widget.FrameLayout;

@EViewGroup
public class SomeGenericViewGroup<T extends CharSequence> extends FrameLayout {

	interface Test<T> {
		void test(T t);
	}

	private T object;
	private Test<T> testInterface;

	public SomeGenericViewGroup(Context context) {
		super(context);
	}

	public SomeGenericViewGroup(Context context, T object, Test<T> testInterface) {
		super(context);
		this.object = object;
		this.testInterface = testInterface;
	}

	@UiThread
	void someGenericMethod(T type) {
	}

	@UiThread
	void someGenericMethod2(T type) {
	}

}
