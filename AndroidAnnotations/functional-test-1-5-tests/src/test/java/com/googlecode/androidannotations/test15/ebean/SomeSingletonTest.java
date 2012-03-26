package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.googlecode.androidannotations.test15.EmptyActivityWithoutLayout_;

@RunWith(AndroidAnnotationsTestRunner.class)
public class SomeSingletonTest {

	@Test
	public void getInstance_returns_same_instance() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeSingleton_ firstInstance = SomeSingleton_.getInstance_(context);
		SomeSingleton_ secondInstance = SomeSingleton_.getInstance_(context);
		assertThat(firstInstance).isSameAs(secondInstance);
	}

	@Test
	public void rebind_does_not_rebind() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeSingleton_ singleton = SomeSingleton_.getInstance_(context);
		
		Context initialContext = singleton.context;
		
		EmptyActivityWithoutLayout_ context2 = new EmptyActivityWithoutLayout_();
		singleton.rebind(context2);
		assertThat(singleton.context).isSameAs(initialContext);
	}

}
