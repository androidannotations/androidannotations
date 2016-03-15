/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package android.support.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ViewPager extends View {
	public ViewPager(Context context) {
		super(context);
	}

	public ViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void addOnPageChangeListener(OnPageChangeListener listener) {

	}

	public interface OnPageChangeListener {
		void onPageScrollStateChanged(int state);

		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		void onPageSelected(int position);
	}
}
