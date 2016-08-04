/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.test.receiver;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.Receiver;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

@EView
public class ViewWithReceiver extends View {

	public boolean action0Received = false;
	public boolean action1Received = false;
	public String action1Extra;
	public boolean action2Received = false;
	public Intent action2Extra;

	public ViewWithReceiver(Context context) {
		super(context);
	}

	public ViewWithReceiver(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Receiver(actions = ReceiverActions.ACTION_0, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
	void receive0() {
		action0Received = true;
	}

	@Receiver(actions = ReceiverActions.ACTION_1, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
	void receive1(@Receiver.Extra("extra") String extra) {
		action1Received = true;
		action1Extra = extra;
	}

	@Receiver(actions = ReceiverActions.ACTION_2, local = true, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
	protected void receive2(@Receiver.Extra Intent extra) {
		action2Received = true;
		action2Extra = extra;
	}
}
