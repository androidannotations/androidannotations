/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.test15;

import android.app.Activity;
import android.widget.SeekBar;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ProgressChange;
import com.googlecode.androidannotations.annotations.TrackingTouchStart;
import com.googlecode.androidannotations.annotations.TrackingTouchStop;

@EActivity(R.layout.seekbars)
public class SeekBarChangeListenedActivity extends Activity {

	@ProgressChange(R.id.seekBar1)
	void m1(SeekBar seekBar) {
	}

	@ProgressChange(R.id.seekBar1)
	void m2(SeekBar seekBar, int progress) {
	}

	@ProgressChange(R.id.seekBar1)
	void m3(SeekBar seekBar, int progress, boolean fromUser) {
	}

	@ProgressChange(R.id.seekBar1)
	void m4(SeekBar seekBar, boolean fromUser, int progress) {
	}

	@ProgressChange({ R.id.seekBar1, R.id.seekBar2 })
	void m5(SeekBar seekBar, boolean fromUser, int progress) {
	}

	@ProgressChange({ R.id.seekBar1, R.id.seekBar2 })
	void m6(Boolean fromUser, SeekBar seekBar, Integer progress) {
	}

	@TrackingTouchStart(R.id.seekBar2)
	@ProgressChange(R.id.seekBar2)
	@TrackingTouchStop(R.id.seekBar2)
	void m7(SeekBar seekBar) {
	}

	@TrackingTouchStop(R.id.seekBar1)
	void m8(SeekBar seekBar) {
	}

	@TrackingTouchStart(R.id.seekBar1)
	void m9(SeekBar seekBar) {
	}

}