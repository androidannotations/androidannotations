package org.androidannotations.test15.inheritance;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;

@RunWith(AndroidAnnotationsTestRunner.class)
public class InheritanceTest {

	@Test
	public void after_inject_mother_calls_first() {
		Child child = Child_.getInstance_(mock(Context.class));
		assertThat(child.motherInitWasCalled).isTrue();
	}
	
	@Test
	public void after_views_mother_calls_first() {
		Child_ child = Child_.getInstance_(mock(Activity.class));
		child.afterSetContentView_();
		assertThat(child.motherInitViewsWasCalled).isTrue();
	}

}
