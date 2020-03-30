/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.api.builder;

import android.app.Activity;
import android.content.Context;

/**
 * Provides additional actions to be added to a start of an {@link Activity}.
 */
public final class PostActivityStarter {

	private Context context;

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 *            the current {@link Context} which is used to start the
	 *            {@link Activity}
	 */
	public PostActivityStarter(Context context) {
		this.context = context;
	}

	/**
	 * Call this to specify an explicit transition animation to perform next.
	 * 
	 * The implementation of this method simply calls
	 * {@link Activity#overridePendingTransition}, if the current context is an
	 * {@link Activity}.
	 * 
	 * @param enterAnim
	 *            A resource ID of the animation resource to use for the incoming
	 *            activity. Use 0 for no animation.
	 * @param exitAnim
	 *            A resource ID of the animation resource to use for the outgoing
	 *            activity. Use 0 for no animation.
	 */
	public void withAnimation(int enterAnim, int exitAnim) {
		if (context instanceof Activity) {
			((Activity) context).overridePendingTransition(enterAnim, exitAnim);
		}
	}
}
