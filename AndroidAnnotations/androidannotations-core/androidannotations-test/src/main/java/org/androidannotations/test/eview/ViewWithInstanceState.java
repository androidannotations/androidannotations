/**
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
package org.androidannotations.test.eview;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.test.instancestate.MyParcelableBean;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

@EView
public class ViewWithInstanceState extends View {

	@InstanceState
	String stateTest = "does it work?";

	String stringState = "does it work?";
	MyParcelableBean beanState = new MyParcelableBean(0);

	public ViewWithInstanceState(Context context) {
		super(context);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putString("stringState", stringState);
		bundle.putParcelable("beanState", beanState);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		stringState = bundle.getString("stringState");
		beanState = bundle.getParcelable("beanState");
	}
}
