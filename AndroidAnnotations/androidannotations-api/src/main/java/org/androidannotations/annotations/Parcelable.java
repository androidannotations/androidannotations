/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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
package org.androidannotations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be used to generate a class as {@link Parcelable}<br/>
 * <br/>
 * Every attributes will be used in the parcel transformation. This rules has
 * some exceptions:<br/>
 * <li>Only attributes with protected or public visibility will be used</li> <li>
 * An attributes with @NonParcelable annotation will be discard</li> <li>A class
 * with @Parcelable annotation but which also implements {@link Parcelable}
 * interface will be discard</li>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Parcelable {

}
