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
package org.androidannotations.test;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PageScrollStateChanged;
import org.androidannotations.annotations.PageScrolled;
import org.androidannotations.annotations.PageSelected;

import android.app.Activity;
import android.support.v4.view.ViewPager;

@EActivity(R.layout.viewpagers)
public class PageChangeListenerActivity extends Activity {

	public ViewPager viewPager = null;
	public int position = 0;
	public float positionOffset = 0f;
	public int positionOffsetPixels = 0;

	public int state = 0;

	@PageScrolled(R.id.viewPager1)
	public void pageScrolled(ViewPager v, int p, float pOffset, int pOffsetPixels) {
		viewPager = v;
		position = p;
		positionOffset = pOffset;
		positionOffsetPixels = pOffsetPixels;
	}

	@PageScrollStateChanged(R.id.viewPager1)
	public void pageScrollStateChanged(ViewPager v, int s) {
		viewPager = v;
		state = s;
	}

	@PageSelected(R.id.viewPager1)
	public void pageSelected(ViewPager v, int p) {
		viewPager = v;
		position = p;
	}

	@PageSelected(R.id.viewPager2)
	public void pageSelected2() {
	}

	@PageScrolled(R.id.viewPager2)
	public void pageScrolled2() {
	}

	@PageScrollStateChanged(R.id.viewPager2)
	public void pageScrollStateChanged2() {
	}

	@PageScrollStateChanged(R.id.viewPager3)
	public void pageScrollStateChanged3(int s, ViewPager v) {
		viewPager = v;
		state = s;
	}

	@PageSelected(R.id.viewPager3)
	public void pageSelected3(int p, ViewPager v) {
		viewPager = v;
		position = p;
	}

	@PageScrolled(R.id.viewPager3)
	public void pageScrolled3(int p) {
		position = p;
	}

	@PageScrolled(R.id.viewPager4)
	public void pageScrolled4(float f, int p) {
		positionOffset = f;
		positionOffsetPixels = p;
	}

}
