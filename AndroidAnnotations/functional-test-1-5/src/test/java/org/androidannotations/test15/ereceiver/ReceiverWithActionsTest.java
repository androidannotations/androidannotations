package org.androidannotations.test15.ereceiver;

import android.content.Intent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ReceiverWithActionsTest {
	private ReceiverWithActions receiver;

	@Before
	public void setup() {
		receiver = new ReceiverWithActions_();
	}

	@Test
	public void onSimpleActionTest() {
		receiver.onReceive(Robolectric.application, new Intent(
				"ACTION_SIMPLE_TEST"));

		assertTrue(receiver.simpleActionReceived);
	}

	@Test
	public void onParameterActionTest() {
		Intent intent = new Intent("ACTION_PARAMETER_TEST");
		intent.putExtra("thisIsMyParameter", "string value");
		receiver.onReceive(Robolectric.application, intent);

		assertTrue(receiver.parameterActionReceived);
		assertEquals("string value", receiver.parameterActionValue);
	}

	@Test
	public void onExtraParameterActionTest() {
		Intent intent = new Intent("ACTION_EXTRA_PARAMETER_TEST");
		intent.putExtra("thisExtraHasAnotherName", "string value");
		receiver.onReceive(Robolectric.application, intent);

		assertTrue(receiver.extraParameterActionReceived);
		assertEquals("string value", receiver.extraParameterActionValue);
	}
}
