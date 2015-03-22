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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Base class for generated {@link android.app.Activity Activity} {@link Intent}
 * builders, which provide a fluent API to build {@link Intent}s and start the
 * generated {@link android.app.Activity Activity}.
 *
 * @param <I>
 *            The actual class, so method chain can return the generated class
 *            and provide generated methods
 */
public abstract class ActivityIntentBuilder<I extends ActivityIntentBuilder<I>> extends IntentBuilder<I> implements ActivityStarter {

	protected Bundle lastOptions;

	/**
	 * Creates a builder for a given {@link android.app.Activity Activity}
	 * class.
	 * 
	 * @param context
	 *            A {@link Context} of the application package implementing this
	 *            class.
	 * @param clazz
	 *            The component class that is to be used for the {@link Intent}.
	 */
	public ActivityIntentBuilder(Context context, Class<?> clazz) {
		super(context, clazz);
	}

	/**
	 * Creates a builder which will append to a previously created
	 * {@link android.content.Intent Intent}.
	 * 
	 * @param context
	 *            A {@link Context} of the application package implementing this
	 *            class.
	 * @param intent
	 *            The previously created {@link Intent} to append to.
	 * 
	 */
	public ActivityIntentBuilder(Context context, Intent intent) {
		super(context, intent);
	}

	@Override
	public final void start() {
		startForResult(-1);
	}

	@Override
	public abstract void startForResult(int requestCode);

	/**
	 * Adds additional options {@link Bundle} to the start method.
	 * 
	 * @param options
	 *            the {@link android.app.Activity Activity} options
	 * @return an {@link ActivityStarter} instance to provide starter methods
	 */
	public ActivityStarter withOptions(Bundle options) {
		lastOptions = options;
		return this;
	}
}
