package com.googlecode.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
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
	public void collection_is_injected() {
		assertThat(activity.collection.size()).isEqualTo(2);
		assertThat(activity.collection.getClass()).isEqualTo(ArrayList.class);
	}

	@Test
	public void collection_is_empty() {
		assertThat(activity.emptyCollection.size()).isEqualTo(0);
	}

	@Test
	public void collection_is_supplied() {
		assertThat(activity.someList.size()).isEqualTo(1);
		assertThat(activity.someList.getClass()).isEqualTo(SomeList_.class);
		assertThat(((SomeList) activity.someList).isAfterViewCalled());
		for (SomeInterface someInterface : activity.someList) {
			assertThat(someInterface.isAfterViewCalled());
		}
	}

	@Test
	public void collection_is_injected_list() {
		assertThat(activity.giveMeList.size()).isEqualTo(0);
		assertThat(activity.giveMeList.getClass()).isEqualTo(ArrayList.class);
	}

	@Test
	public void collection_is_injected_set() {
		assertThat(activity.giveMeSet.size()).isEqualTo(0);
		assertThat(activity.giveMeSet.getClass()).isEqualTo(HashSet.class);
	}
}
