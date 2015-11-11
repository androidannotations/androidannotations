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
package org.androidannotations.internal.core.handler;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.internal.core.model.AndroidRes;

public class ColorStateListResHandler extends ContextCompatAwareResHandler {

	private static final int MIN_SDK_WITH_CONTEXT_GET_COLOR_STATE_LIST = 23;
	private static final String MIN_SDK_PLATFORM_NAME = "M";

	public ColorStateListResHandler(AndroidAnnotationsEnvironment environment) {
		super(AndroidRes.COLOR_STATE_LIST, environment, MIN_SDK_WITH_CONTEXT_GET_COLOR_STATE_LIST, MIN_SDK_PLATFORM_NAME);
	}
}
