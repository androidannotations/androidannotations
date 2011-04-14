package com.googlecode.androidannotations.test15;

//import static org.fest.assertions.Assertions.assertThat;
import static com.googlecode.androidannotations.test15.MyAssertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ViewsInjectedActivityTest {

	private ViewsInjectedActivity_ activity;

	@Before
	public void setup() {
		activity = new ViewsInjectedActivity_();
		activity.onCreate(null);
	}

	@Test
	public void injectionWithConventionIsDone() {
		assertThat(activity.myButton).hasId(R.id.myButton);
	}
	
	@Test
	public void injectionWithConfigurationOverridesConvention() {
		assertThat(activity.someView).hasId(R.id.myTextView);
	}
	
	@Test
	public void multipleInjectionIsSame() {
		assertThat(activity.someView).isSameAs(activity.myTextView);
	}
	
	@Test
	public void unannotatedViewIsNull() {
		assertThat(activity.unboundView).isNull();
	}

}
