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

		@StringRes(R.string.hello) String hello;

	}

	@SharedPref
	public static interface InnerPrefs {

		@DefaultInt(12)
		int intValue();

		@DefaultRes(R.string.hello)
		String stringValue();
	}
}
