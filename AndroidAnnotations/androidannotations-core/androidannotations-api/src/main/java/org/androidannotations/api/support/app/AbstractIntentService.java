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
package org.androidannotations.api.support.app;

import android.app.IntentService;
import android.content.Intent;

/**
 * Convenience class for
 * {@link org.androidannotations.annotations.EIntentService EIntentService}s.
 * This adds an empty implementation of
 * {@link IntentService#onHandleIntent(Intent) onHandleIntent}, so you do not
 * have to in your actual enhanced class.
 */
public abstract class AbstractIntentService extends IntentService {

	public AbstractIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

}
