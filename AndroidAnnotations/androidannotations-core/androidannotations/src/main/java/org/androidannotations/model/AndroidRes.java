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
package org.androidannotations.model;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.BooleanRes;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.ColorStateListRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.annotations.res.IntArrayRes;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.LayoutRes;
import org.androidannotations.annotations.res.MovieRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.res.TextArrayRes;
import org.androidannotations.annotations.res.TextRes;
import org.androidannotations.rclass.IRClass.Res;

public enum AndroidRes {

	STRING(Res.STRING, StringRes.class, "getString", "java.lang.String"), //
	STRING_ARRAY(Res.ARRAY, StringArrayRes.class, "getStringArray", "java.lang.String[]"), //
	ANIMATION(Res.ANIM, AnimationRes.class, "getAnimation", "android.content.res.XmlResourceParser", "android.view.animation.Animation"), //
	HTML(Res.STRING, HtmlRes.class, "getString", "java.lang.CharSequence", "android.text.Spanned"), //
	BOOLEAN(Res.BOOL, BooleanRes.class, "getBoolean", "java.lang.Boolean", "boolean"), //
	COLOR_STATE_LIST(Res.COLOR, ColorStateListRes.class, "getColorStateList", "android.content.res.ColorStateList"), //
	DIMENSION(Res.DIMEN, DimensionRes.class, "getDimension", "java.lang.Float", "float"), //
	DIMENSION_PIXEL_OFFSET(Res.DIMEN, DimensionPixelOffsetRes.class, "getDimensionPixelOffset", "java.lang.Integer", "int"), //
	DIMENSION_PIXEL_SIZE(Res.DIMEN, DimensionPixelSizeRes.class, "getDimensionPixelSize", "java.lang.Integer", "int"), //
	DRAWABLE(Res.DRAWABLE, DrawableRes.class, "getDrawable", "android.graphics.drawable.Drawable"), //
	INT_ARRAY(Res.ARRAY, IntArrayRes.class, "getIntArray", "int[]"), //
	INTEGER(Res.INTEGER, IntegerRes.class, "getInteger", "java.lang.Integer", "int"), //
	LAYOUT(Res.LAYOUT, LayoutRes.class, "getLayout", "android.content.res.XmlResourceParser"), //
	MOVIE(Res.MOVIE, MovieRes.class, "getMovie", "android.graphics.Movie"), //
	TEXT(Res.STRING, TextRes.class, "getText", "java.lang.CharSequence"), //
	TEXT_ARRAY(Res.ARRAY, TextArrayRes.class, "getTextArray", "java.lang.CharSequence"), //
	COLOR(Res.COLOR, ColorRes.class, "getColor", "int", "java.lang.Integer");

	private final Class<? extends Annotation> annotationClass;
	private final String resourceMethodName;
	private final List<String> allowedTypes;
	private final Res rInnerClass;

	AndroidRes(Res rInnerClass, Class<? extends Annotation> annotationClass, String resourceMethodName, String... allowedTypes) {
		this.annotationClass = annotationClass;
		this.resourceMethodName = resourceMethodName;
		this.allowedTypes = Arrays.asList(allowedTypes);
		this.rInnerClass = rInnerClass;
	}

	public Res getRInnerClass() {
		return rInnerClass;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public String getTarget() {
		return annotationClass.getName();
	}

	public String getResourceMethodName() {
		return resourceMethodName;
	}

	public List<String> getAllowedTypes() {
		return allowedTypes;
	}
}
