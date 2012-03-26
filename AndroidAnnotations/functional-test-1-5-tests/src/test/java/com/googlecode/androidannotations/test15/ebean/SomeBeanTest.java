package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.googlecode.androidannotations.test15.EmptyActivityWithoutLayout_;

@RunWith(AndroidAnnotationsTestRunner.class)
public class SomeBeanTest {

	@Test
	public void getInstance_returns_same_instance() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ firstInstance = SomeBean_.getInstance_(context);
		SomeBean_ secondInstance = SomeBean_.getInstance_(context);
		assertThat(firstInstance).isNotSameAs(secondInstance);
	}
	
	@Test
	public void injects_factory_context() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ bean = SomeBean_.getInstance_(context);
		assertThat(bean.context).isSameAs(context);
	}

	@Test
	public void rebind_changes_context() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeBean_ bean = SomeBean_.getInstance_(context);
		
		EmptyActivityWithoutLayout_ context2 = new EmptyActivityWithoutLayout_();
		bean.rebind(context2);
		assertThat(bean.context).isSameAs(context2);
	}

}
