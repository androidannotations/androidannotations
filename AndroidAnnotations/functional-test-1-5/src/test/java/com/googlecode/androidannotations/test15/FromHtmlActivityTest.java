package com.googlecode.androidannotations.test15;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.text.Html;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowHtml;

@RunWith(RobolectricTestRunner.class)
public class FromHtmlActivityTest {
	
	private FromHtmlActivity_ activity;
	
	@Before
	public void setup() {
		Robolectric.bindShadowClass(ShadowHtml.class);

		activity = new FromHtmlActivity_();
		activity.onCreate(null);
	}

	@Test
	public void injectionOfHtmlTest() {
		assertNotNull(activity.textView);
		assertEquals(Html.fromHtml(activity.getString(R.string.hello_html)), activity.textView.getText());
	}
	
	@Test
	public void injectionOfHtmlWithDefaultName() {
		assertNotNull(activity.someView);
		assertEquals(Html.fromHtml(activity.getString(R.string.someView)), activity.someView.getText());
	}
}
