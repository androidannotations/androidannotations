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
 * Use on activity fields to retain instances that are intensive to
 * compute, on configuration changes.
 * 
 * See <a href=
 * "http://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject"
 * >RetainingAnObject</a> in the Android Documentation.<br />
 * <br />
 * Caution: While you can annotate any field, you should never annotate a field
 * that is tied to the Activity, such as a Drawable, an Adapter, a View or any
 * other object that's associated with a Context. If you do, it will leak all
 * the views and resources of the original activity instance. (Leaking resources
 * means that your application maintains a hold on them and they cannot be
 * garbage-collected, so lots of memory can be lost.)<br />
 * <br />
 * This caution doesn't apply to beans annotated with {@link Bean}, because
 * AndroidAnnotations automatically takes care of rebinding their context.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface NonConfigurationInstance {
}
