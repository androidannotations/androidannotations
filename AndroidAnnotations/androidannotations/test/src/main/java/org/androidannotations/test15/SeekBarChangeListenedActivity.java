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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.SeekBarTouchStart;
import org.androidannotations.annotations.SeekBarTouchStop;

import android.app.Activity;
import android.widget.SeekBar;

@EActivity(R.layout.seekbars)
public class SeekBarChangeListenedActivity extends Activity {

	boolean handled;
	int progress;
	boolean fromUser;
	SeekBar seekBar;

	@SeekBarProgressChange(R.id.seekBar1)
	void m1(SeekBar seekBar) {
		handled = true;
	}

	@SeekBarProgressChange(R.id.seekBar1)
	void m2(SeekBar seekBar, int progress) {
		this.progress = progress;
	}

	@SeekBarProgressChange(R.id.seekBar1)
	void m3(SeekBar seekBar, int progress, boolean fromUser) {
		this.fromUser = fromUser;
	}

	@SeekBarProgressChange(R.id.seekBar1)
	void m4(boolean fromUser, int progress) {
	}

	@SeekBarProgressChange({ R.id.seekBar1, R.id.seekBar2 })
	void m5(SeekBar seekBar, boolean fromUser, int progress) {
		this.seekBar = seekBar;
	}

	@SeekBarProgressChange({ R.id.seekBar1, R.id.seekBar2 })
	void m6(Boolean fromUser, Integer progress) {
	}

	@SeekBarProgressChange({ R.id.seekBar1, R.id.seekBar2 })
	void m7() {
	}

	@SeekBarTouchStart(R.id.seekBar2)
	@SeekBarProgressChange(R.id.seekBar2)
	@SeekBarTouchStop(R.id.seekBar2)
	void m8(SeekBar seekBar) {
	}

	@SeekBarTouchStop(R.id.seekBar1)
	void m9(SeekBar seekBar) {
	}

	@SeekBarTouchStop(R.id.seekBar1)
	void m10() {
	}

	@SeekBarTouchStop
	void seekBar1SeekBarTouchStopped() {
		handled = true;
	}

	@SeekBarTouchStart(R.id.seekBar1)
	void m11(SeekBar seekBar) {
	}

	@SeekBarTouchStart(R.id.seekBar1)
	void m12() {
	}

}
