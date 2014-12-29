/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
package org.androidannotations.api.builder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class ActivityIntentBuilder<I extends ActivityIntentBuilder<I>> extends IntentBuilder<I> implements ActivityStarter {

	protected Bundle lastOptions;

	public ActivityIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	public ActivityIntentBuilder(Context context, Intent intent) {
		super(context, intent);
	}

	@Override
	public final void start() {
		startForResult(-1);
	}

	@Override
	public abstract void startForResult(int requestCode);

	public ActivityStarter withOptions(Bundle options) {
		lastOptions = options;
		return this;
	}
}
