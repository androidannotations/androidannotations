package com.googlecode.androidannotations.test15.eview;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.googlecode.androidannotations.test15.EmptyActivityWithoutLayout_;

@RunWith(AndroidAnnotationsTestRunner.class)
public class CustomButtonTest {

	@Test
	public void constructor_parameters_are_transmitted_from_factory_method() {
		Context context = new EmptyActivityWithoutLayout_();
		int parameter = 42;
		CustomButton button = CustomButton_.build(context, parameter);
		assertThat(button.constructorParameter).isEqualTo(parameter);
	}

	@Test
	public void factory_method_builds_inflated_instance() {
		Context context = new EmptyActivityWithoutLayout_();
		CustomButton button = CustomButton_.build(context);
		assertThat(button.afterViewsCalled).isTrue();
	}

}
