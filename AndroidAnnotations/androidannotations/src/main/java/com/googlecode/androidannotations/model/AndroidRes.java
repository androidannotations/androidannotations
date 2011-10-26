/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.annotations.res.BooleanRes;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.googlecode.androidannotations.annotations.res.ColorStateListRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelOffsetRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelSizeRes;
import com.googlecode.androidannotations.annotations.res.DimensionRes;
import com.googlecode.androidannotations.annotations.res.DrawableRes;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.googlecode.androidannotations.annotations.res.IntegerRes;
import com.googlecode.androidannotations.annotations.res.LayoutRes;
import com.googlecode.androidannotations.annotations.res.MovieRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.res.TextArrayRes;
import com.googlecode.androidannotations.annotations.res.TextRes;
import com.googlecode.androidannotations.rclass.IRClass.Res;

public enum AndroidRes {

	STRING(Res.STRING, StringRes.class, "getString", "java.lang.String"), //
	STRING_ARRAY(Res.ARRAY, StringArrayRes.class, "getStringArray", "java.lang.String[]"), //
	ANIMATION(Res.ANIM, AnimationRes.class, "getAnimation", "android.content.res.XmlResourceParser", "android.view.animation.Animation"), //
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

	private final Class<? extends Annotation> target;
	private final String resourceMethodName;
	private final List<String> allowedTypes;
	private final Res rInnerClass;

	AndroidRes(Res rInnerClass, Class<? extends Annotation> target, String resourceMethodName, String... allowedTypes) {
		this.target = target;
		this.resourceMethodName = resourceMethodName;
		this.allowedTypes = Arrays.asList(allowedTypes);
		this.rInnerClass = rInnerClass;
	}

	public Res getRInnerClass() {
		return rInnerClass;
	}

	public Class<? extends Annotation> getTarget() {
		return target;
	}

	public String getResourceMethodName() {
		return resourceMethodName;
	}

	public List<String> getAllowedTypes() {
		return allowedTypes;
	}

	public int idFromElement(Element element) {
		Annotation annotation = element.getAnnotation(target);
		Method valueMethod = target.getMethods()[0];
		try {
			return (Integer) valueMethod.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
