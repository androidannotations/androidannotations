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
package org.androidannotations.processing;

import static com.sun.codemodel.JExpr.invoke;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ViewChangedHolder {

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
