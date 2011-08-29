/**
 * Copyright (C) 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
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
package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.app.Activity;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.res.StringRes;

/**
 * Methods annotated with @{@link BeforeViews} will be called before
 * {@link Activity#setContentView(int)} is called by the generated activity.
 * 
 * When the onCreate() method of your activity is called,
 * {@link Activity#setContentView(int)} has already been called, the views have
 * been injected and the listeners are bound. Sometimes, you might want to
 * execute code before {@link Activity#setContentView(int)} is called. For
 * instance, if you need to call {@link Activity#requestWindowFeature(int)}.
 * 
 * When methods annotated with @{@link BeforeViews} are called,
 * {@link SystemService}s, Resources (e.g. {@link StringRes}) and {@link Extra}s
 * are already injected. However views are not injected yet, so beware of
 * {@link NullPointerException}s.
 * 
 * The method may have zero or one parameter, that must be a {@link Bundle}.
 * 
 * There may be several methods annotated with @{@link BeforeViews} in the same
 * activity.
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface BeforeViews {
}