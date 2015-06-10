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
package org.androidannotations.holder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class OnSeekBarChangeListenerHolder  {

	private EComponentWithViewSupportHolder holder;
	private JDefinedClass listenerClass;
	private JBlock onProgressChangedBody;
	private JVar onProgressChangedSeekBarParam;
	private JVar onProgressChangedProgressParam;
	private JVar onProgressChangedFromUserParam;
	private JBlock onStartTrackingTouchBody;
	private JVar onStartTrackingTouchSeekBarParam;
	private JBlock onStopTrackingTouchBody;
	private JVar onStopTrackingTouchSeekBarParam;

	public OnSeekBarChangeListenerHolder(EComponentWithViewSupportHolder holder, JDefinedClass onSeekbarChangeListenerClass) {
		this.holder = holder;
		listenerClass = onSeekbarChangeListenerClass;
		createOnProgressChanged();
		createOnStartTrackingTouch();
		createOnStopTrackingTouch();
	}

	private void createOnProgressChanged() {
		JMethod onProgressChangedMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onProgressChanged");
		onProgressChangedMethod.annotate(Override.class);
		onProgressChangedBody = onProgressChangedMethod.body();
		onProgressChangedSeekBarParam = onProgressChangedMethod.param(holder.classes().SEEKBAR, "seekBar");
		onProgressChangedProgressParam = onProgressChangedMethod.param(holder.codeModel().INT, "progress");
		onProgressChangedFromUserParam = onProgressChangedMethod.param(holder.codeModel().BOOLEAN, "fromUser");
	}

	private void createOnStartTrackingTouch() {
		JMethod onStartTrackingTouchMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onStartTrackingTouch");
		onStartTrackingTouchMethod.annotate(Override.class);
		onStartTrackingTouchBody = onStartTrackingTouchMethod.body();
		onStartTrackingTouchSeekBarParam = onStartTrackingTouchMethod.param(holder.classes().SEEKBAR, "seekBar");
	}

	private void createOnStopTrackingTouch() {
		JMethod onStopTrackingTouchMethod = listenerClass.method(JMod.PUBLIC, holder.codeModel().VOID, "onStopTrackingTouch");
		onStopTrackingTouchMethod.annotate(Override.class);
		onStopTrackingTouchBody = onStopTrackingTouchMethod.body();
		onStopTrackingTouchSeekBarParam = onStopTrackingTouchMethod.param(holder.classes().SEEKBAR, "seekBar");
	}

	public JBlock getOnProgressChangedBody() {
		return onProgressChangedBody;
	}

	public JVar getOnProgressChangedSeekBarParam() {
		return onProgressChangedSeekBarParam;
	}

	public JVar getOnProgressChangedProgressParam() {
		return onProgressChangedProgressParam;
	}

	public JVar getOnProgressChangedFromUserParam() {
		return onProgressChangedFromUserParam;
	}

	public JBlock getOnStartTrackingTouchBody() {
		return onStartTrackingTouchBody;
	}

	public JVar getOnStartTrackingTouchSeekBarParam() {
		return onStartTrackingTouchSeekBarParam;
	}

	public JBlock getOnStopTrackingTouchBody() {
		return onStopTrackingTouchBody;
	}

	public JVar getOnStopTrackingTouchSeekBarParam() {
		return onStopTrackingTouchSeekBarParam;
	}
}
