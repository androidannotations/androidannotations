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
package org.androidannotations.receiver;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;

import android.app.Fragment;
import android.content.Intent;

@EFragment
public class FragmentWithValidReceiver extends Fragment {

	@Receiver(actions = "org.androidannotations")
	protected void registeredOnCreateOnDestroy() {

	}

	@Receiver(actions = "org.androidannotations", registerAt = Receiver.RegisterAt.OnAttachOnDetach)
	protected void registeredOnAttachOnDetach(Intent intent) {

	}

	@Receiver(actions = "org.androidannotations", registerAt = Receiver.RegisterAt.OnStartOnStop)
	protected void registeredOnStartOnStop() {

	}

	@Receiver(actions = "org.androidannotations", registerAt = Receiver.RegisterAt.OnResumeOnPause)
	protected void registeredOnResumeOnPause(Intent intent) {

	}

}
