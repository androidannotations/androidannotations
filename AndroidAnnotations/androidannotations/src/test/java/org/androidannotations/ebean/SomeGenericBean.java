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
package org.androidannotations.ebean;

import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

@EBean
public class SomeGenericBean<T> {

	@Background
	void someMethod(List<? super T> list) {
	}

	void someOtherMethod(List<? super T> list) {
	}

	@Background
	<N extends T> void someParameterizedMethod(List<? super N> lst, List<? extends N> lst2) {
	}

	@UiThread
	<U, S extends Number> void emptyUiMethod(List<? extends T> param, List<? super S> param2) {
	}

}
