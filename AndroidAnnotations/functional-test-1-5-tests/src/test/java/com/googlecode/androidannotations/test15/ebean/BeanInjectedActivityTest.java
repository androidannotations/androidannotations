package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class BeanInjectedActivityTest {
	
	private BeanInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new BeanInjectedActivity_();
		activity.onCreate(null);
		
	}

	@Test
	public void dependency_is_injected() {
		assertThat(activity.dependency).isNotNull();
	}
	
	@Test
	public void dependency_with_annotation_value_is_injected() {
		assertThat(activity.interfaceDependency).isNotNull();
	}
	
	@Test
	public void dependency_with_annotation_value_is_of_annotation_value_type() {
		assertThat(activity.interfaceDependency).isInstanceOf(SomeImplementation.class);
	}
	
	@Test
	public void singleton_dependency_is_same_reference() {
		SingletonDependency initialDependency = activity.singletonDependency;
		
		BeanInjectedActivity_ newActivity = new BeanInjectedActivity_();
		newActivity.onCreate(null);
		
		assertThat(newActivity.singletonDependency).isSameAs(initialDependency);
	}
	
}
