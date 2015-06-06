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
import android.content.Intent;

/**
 * Provides methods to start {@link android.app.IntentService IntentService}s.
 */
public interface IntentServiceStarter {

	/**
	 * Starts the {@link android.app.IntentService IntentService} by calling
	 * {@link android.content.Context#startService(android.content.Intent)
	 * startService} on the previously given {@link android.content.Context
	 * Context}.
	 * 
	 * @return the result of
	 *         {@link android.content.Context#startService(android.content.Intent)
	 *         startService}
	 */
	ComponentName start();

	/**
	 * Accessor for the built {@link Intent}.
	 * 
	 * @return the created {@link Intent}.
	 */
	Intent get();

}
