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
package org.androidannotations.test15.innerclasses;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.test15.R;
import org.androidannotations.test15.ebean.SomeImplementation;

@EBean
public class BeanWithInnerEnhancedClasses {

	@Pref
	BeanWithInnerEnhancedClasses_.InnerPrefs_ innerPrefs;

	@Bean
	InnerEnhancedBean innerEnhancedBean;

	@Bean
	SomeImplementation someImplementation;

	@EBean
	public static class InnerEnhancedBean {

		@StringRes(R.string.hello)
		String hello;

	}

	@SharedPref
	public interface InnerPrefs {

		@DefaultInt(12)
		int intValue();

		@DefaultRes(R.string.hello)
		String stringValue();
	}
}
