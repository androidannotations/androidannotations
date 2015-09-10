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
package org.androidannotations.api.builder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public abstract class ServiceIntentBuilder<I extends ServiceIntentBuilder<I>> extends IntentBuilder<I> {

	public ServiceIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	public ServiceIntentBuilder(Context context, Intent intent) {
		super(context, intent);
	}

	public ComponentName start() {
		return context.startService(intent);
	}

	public boolean stop() {
		return context.stopService(intent);
	}
}
