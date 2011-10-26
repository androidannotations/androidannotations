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
package com.googlecode.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.os.Bundle;

/**
 * Methods annotated with @{@link BeforeCreate} will be called before
 * <b>super.onCreate()</b> is called by the generated activity.
 * 
 * The method may have zero or one parameter, that must be a {@link Bundle}.
 * 
 * There may be several methods annotated with @{@link BeforeCreate} in the same
 * activity.
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface BeforeCreate {
}