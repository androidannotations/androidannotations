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
package org.androidannotations.api.view;

import java.util.LinkedHashSet;
import java.util.Set;

public class OnViewChangedNotifier {

	private static OnViewChangedNotifier currentNotifier;

	public static OnViewChangedNotifier replaceNotifier(OnViewChangedNotifier notifier) {
		OnViewChangedNotifier previousNotifier = currentNotifier;
		currentNotifier = notifier;
		return previousNotifier;
	}

	public static void registerOnViewChangedListener(OnViewChangedListener listener) {
		if (currentNotifier != null) {
			currentNotifier.listeners.add(listener);
		}
	}

	private final Set<OnViewChangedListener> listeners = new LinkedHashSet<>();

	public void notifyViewChanged(HasViews hasViews) {
		for (OnViewChangedListener listener : listeners) {
			listener.onViewChanged(hasViews);
		}
	}

}