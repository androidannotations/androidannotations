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
package org.androidannotations.test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;
import static org.robolectric.Robolectric.setupActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.support.v4.view.ViewPager;

@RunWith(RobolectricTestRunner.class)
public class PageChangeListenerActivityTest {
	private PageChangeListenerActivity_ activity;
	private ViewPager viewPager;
	private ViewPager.OnPageChangeListener listener;

	@Before
	public void setUp() {
		activity = setupActivity(PageChangeListenerActivity_.class);
	}

	private void initViewPager(int viewPagerId) {
		viewPager = (ViewPager) activity.findViewById(viewPagerId);
		listener = field("mOnPageChangeListener").ofType(ViewPager.OnPageChangeListener.class).in(viewPager).get();
	}

	private void assertReadyForTest() {
		assertViewPagerIsNull();
		assertPositionIsNotSet();
		assertPositionOffsetIsNotSet();
		assertPositionOffsetPixelsIsNotSet();
		assertStateIsNotSet();
	}

	private void assertViewPagerIsNull() {
		assertThat(activity.viewPager).isNull();
	}

	private void assertPositionIsNotSet() {
		assertThat(activity.position).isEqualTo(0);
	}

	private void assertPositionOffsetPixelsIsNotSet() {
		assertThat(activity.positionOffsetPixels).isEqualTo(0);
	}

	private void assertPositionOffsetIsNotSet() {
		assertThat(activity.positionOffset).isEqualTo(0f);
	}

	private void assertStateIsNotSet() {
		assertThat(activity.state).isEqualTo(0);
	}

	@Test
	public void handlePageSelected() {
		initViewPager(R.id.viewPager1);

		assertViewPagerIsNull();
		assertPositionIsNotSet();

		int position = 100;

		listener.onPageSelected(position);

		assertThat(activity.viewPager).isEqualTo(viewPager);
		assertThat(activity.position).isEqualTo(position);
	}

	@Test
	public void handlePageScrolled() {
		initViewPager(R.id.viewPager1);
		assertReadyForTest();

		int position = 101;
		float positionOffset = 102f;
		int positionOffsetPixels = 103;

		listener.onPageScrolled(position, positionOffset, positionOffsetPixels);

		assertThat(activity.viewPager).isEqualTo(viewPager);
		assertThat(activity.position).isEqualTo(position);
		assertThat(activity.positionOffset).isEqualTo(positionOffset);
		assertThat(activity.positionOffsetPixels).isEqualTo(positionOffsetPixels);
	}

	@Test
	public void handlePageScrollStateChanged() {
		initViewPager(R.id.viewPager1);
		assertReadyForTest();

		int state = 104;

		listener.onPageScrollStateChanged(state);

		assertThat(activity.viewPager).isEqualTo(viewPager);
		assertThat(activity.state).isEqualTo(state);
	}

	@Test
	public void handleNoPageSelectedParameter() {
		initViewPager(R.id.viewPager2);
		assertReadyForTest();

		listener.onPageSelected(1);

		assertViewPagerIsNull();
		assertPositionIsNotSet();
	}

	@Test
	public void handleNoPageScrolledParameter() {
		initViewPager(R.id.viewPager2);
		assertReadyForTest();

		listener.onPageScrolled(1, 1f, 2);

		assertViewPagerIsNull();
		assertPositionIsNotSet();
		assertPositionOffsetIsNotSet();
		assertPositionOffsetPixelsIsNotSet();
	}

	@Test
	public void handleNoPageScrollStateChangedParameter() {
		initViewPager(R.id.viewPager2);
		assertReadyForTest();

		listener.onPageScrollStateChanged(1);

		assertViewPagerIsNull();
		assertStateIsNotSet();
	}

	@Test
	public void handleAnyOrderPageSelectedParameter() {
		initViewPager(R.id.viewPager3);
		assertReadyForTest();

		int position = 105;

		listener.onPageSelected(position);

		assertThat(activity.viewPager).isEqualTo(viewPager);
		assertThat(activity.position).isEqualTo(position);
	}

	@Test
	public void handleAnyOrderPageScrollStateChangedParameter() {
		initViewPager(R.id.viewPager3);
		assertReadyForTest();

		int state = 106;

		listener.onPageScrollStateChanged(state);

		assertThat(activity.viewPager).isEqualTo(viewPager);
		assertThat(activity.state).isEqualTo(state);
	}

	@Test
	public void handlePageScrollFirstIntBecomesPositionParameter() {
		initViewPager(R.id.viewPager3);
		assertReadyForTest();

		int position = 107;

		listener.onPageScrolled(position, 108f, 109);

		assertViewPagerIsNull();
		assertThat(activity.position).isEqualTo(position);
		assertPositionOffsetIsNotSet();
		assertPositionOffsetPixelsIsNotSet();
	}

	@Test
	public void handleIntAfterPositionOffsetBecomesPositionOffsetPixels() {
		initViewPager(R.id.viewPager4);
		assertReadyForTest();

		float positionOffset = 111f;
		int positionOffsetPixels = 112;

		listener.onPageScrolled(110, positionOffset, positionOffsetPixels);

		assertViewPagerIsNull();
		assertPositionIsNotSet();
		assertThat(activity.positionOffset).isEqualTo(positionOffset);
		assertThat(activity.positionOffsetPixels).isEqualTo(positionOffsetPixels);
	}

}
