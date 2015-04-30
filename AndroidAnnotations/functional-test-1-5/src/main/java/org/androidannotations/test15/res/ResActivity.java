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
package org.androidannotations.test15.res;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.test15.R;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.text.Spanned;
import android.view.animation.Animation;

@EActivity(R.layout.main)
public class ResActivity extends Activity {

	// CHECKSTYLE:OFF

	// @AnimationRes
	XmlResourceParser fade_in;

	// @AnimationRes
	Animation fadein;

	@StringRes
	String injected_string;

	@StringRes
	String injectedString;

	@HtmlRes
	Spanned helloHtml;

	@HtmlRes(R.string.hello_html)
	CharSequence htmlInjected;

	// CHECKSTYLE:ON
}
