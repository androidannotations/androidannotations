/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.test.res;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.test.R;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.view.animation.Animation;

@EActivity(R.layout.main)
public class ResActivity extends Activity {

	// CHECKSTYLE:OFF

	// @AnimationRes
	XmlResourceParser fade_in;

	@AnimationRes
	Animation fadein;

	@DrawableRes
	Drawable icon;

	@StringRes
	String injected_string;

	@StringRes
	String injectedString;

	@HtmlRes
	Spanned helloHtml;

	@HtmlRes(R.string.hello_html)
	CharSequence htmlInjected;

	String methodInjectedString;
	String multiInjectedString;
	Drawable methodInjectedDrawable;
	Drawable multiInjectedDrawable;
	Spanned methodInjectedHtml;
	Spanned multiInjectedHtml;
	Animation methodInjectedAnimation;
	Animation multiInjectedAnimation;

	@StringRes
	void injectedString(String anythingWeWant) {
		methodInjectedString = anythingWeWant;
	}

	void stringResources(@StringRes String injectedString, @StringRes String injected_string) {
		multiInjectedString = injectedString;
	}

	@DrawableRes
	void icon(Drawable anythingWeWant) {
		methodInjectedDrawable = anythingWeWant;
	}

	void drawableResources(@DrawableRes Drawable icon, @DrawableRes(resName = "icon") Drawable resNameIcon) {
		multiInjectedDrawable = icon;
	}

	@HtmlRes
	void helloHtml(Spanned anythingWeWant) {
		methodInjectedHtml = anythingWeWant;
	}

	void htmlResources(@HtmlRes Spanned helloHtml, @HtmlRes(resName = "helloHtml") CharSequence htmlInjected) {
		multiInjectedHtml = helloHtml;
	}

	@AnimationRes
	void fadeIn(Animation anythingWeWant) {
		methodInjectedAnimation = anythingWeWant;
	}

	void animResources(@AnimationRes Animation fadein, @AnimationRes(resName = "fade_in") Animation animInjected) {
		multiInjectedAnimation = fadein;
	}
	// CHECKSTYLE:ON

}
