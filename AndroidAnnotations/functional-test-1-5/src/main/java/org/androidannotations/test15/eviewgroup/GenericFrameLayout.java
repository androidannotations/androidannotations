/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.test15.eviewgroup;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.test15.R;

import android.content.Context;
import android.util.AttributeSet;

@EViewGroup(R.layout.component)
public class GenericFrameLayout<T extends Number> extends CustomFrameLayout {

	public GenericFrameLayout(Context context, int i) {
		super(context, i);
	}

	public GenericFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
