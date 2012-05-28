package com.googlecode.androidannotations.test15.efragment;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;

/**
 * Those test are disabled for now, we need to update Robolectric version for
 * fragment support, however we'll have to solve other issues to do so.
 */
// @RunWith(AndroidAnnotationsTestRunner.class)
public class MyFragmentActivityTest {

	private MyFragmentActivity_ activity;

	// @Before
	public void setup() {
		activity = new MyFragmentActivity_();
		activity.onCreate(null);
	}

	// @Test
	public void can_inject_native_fragment_with_default_id() {
		assertThat(activity.myFragment).isNotNull();
	}

	// @Test
	public void can_inject_native_fragment_with_id() {
		assertThat(activity.myFragment2).isNotNull();
	}

	// @Test
	public void can_inject_support_fragment_with_default_id() {
		assertThat(activity.mySupportFragment).isNotNull();
	}

	// @Test
	public void can_inject_support_fragment_with_id() {
		assertThat(activity.mySupportFragment2).isNotNull();
	}

}
