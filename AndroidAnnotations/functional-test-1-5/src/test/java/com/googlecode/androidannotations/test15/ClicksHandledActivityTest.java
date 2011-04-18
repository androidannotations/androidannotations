package com.googlecode.androidannotations.test15;

import static org.fest.assertions.Assertions.assertThat;
import static com.googlecode.androidannotations.test15.MyAssertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ClicksHandledActivityTest {

	private ClicksHandledActivity_ activity;

	@Before
	public void setup() {
		activity = new ClicksHandledActivity_();
		activity.onCreate(null);
	}

	@Test
	public void handlingWithConvention() {
		assertThat(activity.conventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.conventionButton).performClick();
		
		assertThat(activity.conventionButtonClicked).isTrue();
	}
	
	@Test
	public void handlingWithExtendedConvention() {
		assertThat(activity.extendedConventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.extendedConventionButton).performClick();
		
		assertThat(activity.extendedConventionButtonClicked).isTrue();
	}
	
	@Test
	public void handlingWithConfigurationOverConvention() {
		assertThat(activity.overridenConventionButtonClicked).isFalse();
		
		activity.findViewById(R.id.configurationOverConventionButton).performClick();
		
		assertThat(activity.overridenConventionButtonClicked).isTrue();
	}
	
	@Test
	public void unannotatedButtonIsNotHandled() {
		activity.findViewById(R.id.unboundButton).performClick();
		
		assertThat(activity.unboundButtonClicked).isFalse();
	}
	
	@Test
	public void viewArgumentIsGiven() {
		assertThat(activity.viewArgument).isNull();
		
		activity.findViewById(R.id.buttonWithViewArgument).performClick();
		
		assertThat(activity.viewArgument).hasId(R.id.buttonWithViewArgument);
	}
	

}
