/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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
package org.androidannotations.test15;

import android.app.Activity;
import android.view.View;

public abstract class EventsHandledAbstractActivity extends Activity {

	protected View viewArgument;

	protected boolean avoidStackOverflowEventHandled;
	protected boolean conventionButtonEventHandled;
	protected boolean snakeCaseButtonEventHandled;
	protected boolean extendedConventionButtonEventHandled;
	protected boolean overridenConventionButtonEventHandled;
	protected boolean unboundButtonEventHandled;
	protected boolean multipleButtonsEventHandled;
	protected boolean libResButtonEventHandled;

}
