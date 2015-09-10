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
package org.androidannotations.api.support.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Convenience class for
 * {@link org.androidannotations.annotations.ReceiverAction ReceiverAction}. If
 * you extend from it, this adds an empty implementation of
 * {@link BroadcastReceiver#onReceive(Context, Intent) onReceive}, so you do not
 * have to do in your actual class.
 */
public abstract class AbstractBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

	}

}
