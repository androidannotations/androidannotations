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
package org.androidannotations.test15.receiver;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;

import android.app.Fragment;

@EFragment
public class FragmentWithReceiver extends Fragment {

	public static final String RECEIVER_ACTION = "org.androidannotations.ACTION_1";

	public boolean defaultReceiverCalled;
	public boolean onAttachReceiverCalled;
	public boolean onStartReceiverCalled;
	public boolean onResumeReceiverCalled;

	@Receiver(actions = RECEIVER_ACTION)
	protected void onActionOnCreate() {
		defaultReceiverCalled = true;
	}

	@Receiver(actions = RECEIVER_ACTION, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
	protected void onActionOnAttach() {
		onAttachReceiverCalled = true;
	}

	@Receiver(actions = RECEIVER_ACTION, registerAt = Receiver.RegisterAt.OnStartOnStop)
	protected void onActionOnStart() {
		onStartReceiverCalled = true;
	}

	@Receiver(actions = RECEIVER_ACTION, registerAt = Receiver.RegisterAt.OnResumeOnPause, local = true)
	protected void onActionOnResume() {
		onResumeReceiverCalled = true;
	}

}
