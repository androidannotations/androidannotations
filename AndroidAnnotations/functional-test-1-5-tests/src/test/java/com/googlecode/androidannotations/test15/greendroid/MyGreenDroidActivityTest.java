package com.googlecode.androidannotations.test15.greendroid;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.androidannotations.test15.AndroidAnnotationsTestRunner;
import com.googlecode.androidannotations.test15.R;

@RunWith(AndroidAnnotationsTestRunner.class)
public class MyGreenDroidActivityTest {
	
	@Test
	public void when_layout_defined_then_onCreate_calls_setActionBarContentView_with_layout_id_value() {
		MyGreenDroidActivity_ activity = new MyGreenDroidActivity_();
		activity.onCreate(null);
		assertThat(activity.layoutResID).isEqualTo(R.layout.main);
	}
	
	@Test
	public void afterViews_method_is_called_in_setActionBarContentView() {
		MyGreenDroidActivity_ activity = new MyGreenDroidActivity_();
		activity.setActionBarContentView(0);
		assertThat(activity.afterViewsCalled).isTrue();
	}

}
