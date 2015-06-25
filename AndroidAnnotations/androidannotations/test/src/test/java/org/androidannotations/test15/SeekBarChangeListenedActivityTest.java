/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.setupActivity;
import static org.robolectric.Robolectric.shadowOf_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSeekBar;

import android.widget.SeekBar;

@RunWith(RobolectricTestRunner.class)
public class SeekBarChangeListenedActivityTest {

	private SeekBarChangeListenedActivity_ activity;
	private SeekBar seekBar;
	private ShadowSeekBar shadowSeekBar;

	@Before
	public void setUp() {
		activity = setupActivity(SeekBarChangeListenedActivity_.class);
		seekBar = (SeekBar) activity.findViewById(R.id.seekBar1);
		shadowSeekBar = shadowOf_(seekBar);
	}

	@Test
	public void testActionHandled() {
		assertThat(activity.handled).isFalse();

		shadowSeekBar.getOnSeekBarChangeListener().onProgressChanged(seekBar, 0, false);

		assertThat(activity.handled).isTrue();
	}

	@Test
	public void testSeekBarPassed() {
		assertThat(activity.seekBar).isNull();

		shadowSeekBar.getOnSeekBarChangeListener().onProgressChanged(seekBar, 0, false);

		assertThat(activity.seekBar).isEqualTo(seekBar);
	}

	@Test
	public void testProgressPassed() {
		assertThat(activity.progress).isZero();

		int progress = 45;

		shadowSeekBar.getOnSeekBarChangeListener().onProgressChanged(seekBar, progress, false);

		assertThat(activity.progress).isEqualTo(progress);
	}

	@Test
	public void testFromUserPassed() {
		assertThat(activity.fromUser).isFalse();

		shadowSeekBar.getOnSeekBarChangeListener().onProgressChanged(seekBar, 0, true);

		assertThat(activity.fromUser).isTrue();
	}

	@Test
	public void testSeekBarTouchStopNamingConvention() {
		assertThat(activity.handled).isFalse();

		shadowSeekBar.getOnSeekBarChangeListener().onStopTrackingTouch(seekBar);

		assertThat(activity.handled).isTrue();
	}

}
