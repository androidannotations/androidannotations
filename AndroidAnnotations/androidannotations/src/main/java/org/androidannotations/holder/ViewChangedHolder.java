/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import com.sun.codemodel.*;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;

public class ViewChangedHolder {

	public static ViewChangedHolder createViewChangedHolder(EComponentHolder holder) {
		holder.getGeneratedClass()._implements(OnViewChangedListener.class);
		JMethod onViewChanged = holder.getGeneratedClass().method(PUBLIC, holder.codeModel().VOID, "onViewChanged");
		onViewChanged.annotate(Override.class);
		JVar onViewChangedHasViewsParam = onViewChanged.param(HasViews.class, "hasViews");
		JClass notifierClass = holder.refClass(OnViewChangedNotifier.class);
		holder.getInit().body().staticInvoke(notifierClass, "registerOnViewChangedListener").arg(_this());
		return new ViewChangedHolder(onViewChanged, onViewChangedHasViewsParam);
	}

	private JMethod onViewChanged;
	private JVar onViewChangedHasViewsParam;

	public ViewChangedHolder(JMethod onViewChanged, JVar onViewChangedHasViewsParam) {
		this.onViewChanged = onViewChanged;
		this.onViewChangedHasViewsParam = onViewChangedHasViewsParam;
	}

	public JBlock body() {
		return onViewChanged.body();
	}

	public JInvocation findViewById(JFieldRef idRef) {
		JInvocation findViewById = invoke(onViewChangedHasViewsParam, "findViewById");
		findViewById.arg(idRef);
		return findViewById;

	}

}
