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

import org.androidannotations.annotations.PageScrollStateChanged;
import org.androidannotations.annotations.PageScrolled;
import org.androidannotations.annotations.PageSelected;

public class ActivityWithViewPagerWrongParams {
	@PageSelected(R.id.myViewPager)
	public void nonExistTypeForPageSelected(String text) {
	}

	@PageScrolled(R.id.myViewPager)
	public void nonExistTypeForPageScrolled(String text) {
	}

	@PageScrollStateChanged(R.id.myViewPager)
	public void nonExistTypeForPageScrollStateChanged(String text) {
	}

	@PageScrolled(R.id.myViewPager2)
	public void nonExistIntName(int text) {
	}
}
