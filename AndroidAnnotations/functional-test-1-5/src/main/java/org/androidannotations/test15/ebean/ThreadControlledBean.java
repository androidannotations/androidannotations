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
package org.androidannotations.test15.ebean;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeBackground;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.UiThread;

@EBean
public class ThreadControlledBean {

	public static final String SERIAL1 = "serial1";
	public static final String SERIAL2 = "serial2";

	@SupposeUiThread
	public void uiSupposed() {
	}

	@SupposeBackground
	public void backgroundSupposed() {
	}

	@SupposeBackground(serial = { SERIAL1, SERIAL2 })
	public void serialBackgroundSupposed() {
	}

	@SupposeUiThread
	@UiThread
	public void uiSupposedAndUi(Runnable delegate) {
		delegate.run();
	}

	@SupposeBackground(serial = SERIAL1)
	@Background(serial = SERIAL2)
	public void backgroundSupposeAndBackground(Runnable delegate) {
		delegate.run();
	}

}
