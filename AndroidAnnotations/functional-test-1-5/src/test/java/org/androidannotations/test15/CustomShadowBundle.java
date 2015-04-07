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
package org.androidannotations.test15;

import static org.robolectric.Robolectric.directlyOn;

import java.util.Arrays;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.internal.ReflectionHelpers;
import org.robolectric.shadows.ShadowBundle;

import android.os.Bundle;
import android.os.Parcelable;

/***
 * Workaround for https://github.com/robolectric/robolectric/issues/1440
 */
@Implements(Bundle.class)
public class CustomShadowBundle extends ShadowBundle {

	@RealObject
	private Bundle realObject;

	@Implementation
	public Parcelable[] getParcelableArray(String key) {
		Parcelable[] array = directlyOn(realObject, Bundle.class, "getParcelableArray", new ReflectionHelpers.ClassParameter<String>(String.class, key));

		if (array == null) {
			return null;
		}

		return Arrays.copyOf(array, array.length, Parcelable[].class);
	}
}
