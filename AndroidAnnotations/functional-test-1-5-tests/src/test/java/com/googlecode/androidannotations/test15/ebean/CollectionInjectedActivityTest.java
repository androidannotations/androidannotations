package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;

@RunWith(AndroidAnnotationsTestRunner.class)
public class CollectionInjectedActivityTest {

	private CollectionInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new CollectionInjectedActivity_();
		activity.onCreate(null);

	}

	@Test
	public void bean_is_optional() {
		assertThat(activity.collection.size()).isEqualTo(2);
		assertThat(activity.collection.getClass()).isEqualTo(ArrayList.class);
	}

	@Test
	public void collection_is_empty() {
		assertThat(activity.emptyCollection.size()).isEqualTo(0);
	}

	private <T> void verify_collection(Collection<SomeInterface> collection,
			Class<T> expectedClass) {
		assertThat(collection.size()).isEqualTo(2);
		assertThat(collection.getClass()).isEqualTo(expectedClass);
		for (SomeInterface someInterface : collection) {
			assertThat(someInterface.isAfterViewCalled());
		}
	}

	@Test
	public void bean_value_present_and_beans_are_supplied() {
		verify_collection(activity.someListInterface,
				SomeComplexGenericList_.class);
	}

	@Test
	public void old_fashion_bean_and_beans_are_supplied() {
		verify_collection(activity.someListImpl, SomeComplexGenericList_.class);
	}

	@Test
	public void bean_with_simple_inheritance() {
		verify_collection(activity.someOtherList, SomeSimpleList_.class);
	}

	@Test
	public void default_ArrayList() {
		assertThat(activity.giveMeList.size()).isEqualTo(0);
		assertThat(activity.giveMeList.getClass()).isEqualTo(ArrayList.class);
	}

	@Test
	public void default_HashSet() {
		assertThat(activity.giveMeSet.size()).isEqualTo(0);
		assertThat(activity.giveMeSet.getClass()).isEqualTo(HashSet.class);
	}
}
