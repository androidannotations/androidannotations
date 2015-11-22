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
package org.androidannotations.generation;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PageScrollStateChanged;
import org.androidannotations.annotations.PageScrolled;
import org.androidannotations.annotations.PageSelected;

import android.app.Activity;
import android.support.v4.view.ViewPager;

@EActivity
public class ActivityWithViewPager extends Activity {
	@PageScrollStateChanged(R.id.myViewPagerWithAllMethods)
	public void pageScrollStateChanged() {
	}

	@PageScrollStateChanged(R.id.myViewPagerWithPageScrollStateChanged)
	public void pageScrollStateChangedOnly() {
	}

	@PageScrollStateChanged(R.id.myViewPager)
	public void pageScrollStateChangedWithParams(ViewPager v, int s) {
	}

	@PageScrollStateChanged(R.id.myViewPager2)
	public void pageScrollStateChangedWithOnlyViewPagerParams(ViewPager v) {
	}

	@PageScrollStateChanged(R.id.myViewPager3)
	public void pageScrollStateChangedWithOnlyIntParams(int state) {
	}

	@PageScrollStateChanged
	public void myViewPagerWithNoRes1() {
	}

	@PageScrollStateChanged({ R.id.myViewPagerWithMultipleRes1, R.id.myViewPagerWithMultipleRes2 })
	public void pageScrollStateChangedWithMultipleRes() {
	}

	@PageScrolled(R.id.myViewPagerWithAllMethods)
	public void pageScrolled() {
	}

	@PageScrolled(R.id.myViewPagerWithPageScrolled)
	public void pageScrolledOnly() {
	}

	@PageScrolled(R.id.myViewPager)
	public void pageScrolledWithParams(ViewPager viewPager, int int1, float float1, int int2) {
	}

	@PageScrolled(R.id.myViewPager2)
	public void pageScrolledWithOnlyViewPager(ViewPager view) {
	}

	@PageScrolled(R.id.myViewPager3)
	public void pageScrolledWithOnlyInt(int position, int positionOffsetPixels) {
	}

	@PageScrolled
	public void myViewPagerWithNoRes2(float f, int positionOffsetPixels) {
	}

	@PageScrolled({ R.id.myViewPagerWithMultipleRes1, R.id.myViewPagerWithMultipleRes2 })
	public void pageScrolledWithMultipleRes() {
	}

	@PageSelected(R.id.myViewPagerWithAllMethods)
	public void pageSelected() {
	}

	@PageSelected(R.id.myViewPagerWithPageSelected)
	public void pageSelectedOnly() {
	}

	@PageSelected(R.id.myViewPager)
	public void pageSelectedWithParams(ViewPager position, int view) {
	}

	@PageSelected(R.id.myViewPager2)
	public void pageSelectedWithOnlyViewPagerParams(ViewPager position) {
	}

	@PageSelected(R.id.myViewPager3)
	public void pageSelectedWithOnlyIntParams(int position) {
	}

	@PageSelected
	public void myViewPagerWithNoRes3() {
	}

	@PageSelected({ R.id.myViewPagerWithMultipleRes1, R.id.myViewPagerWithMultipleRes2 })
	public void pageSelectedWithMultipleRes() {
	}
}
