/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;

public class FoundViewHolder {

	private GeneratedClassHolder holder;
	private JClass viewClass;
	private JExpression view;
	private JBlock ifNotNullBlock;
	private boolean ifNotNullCreated = false;

	public FoundViewHolder(GeneratedClassHolder holder, JClass viewClass, JExpression view, JBlock block) {
		this.holder = holder;
		this.viewClass = viewClass;
		this.view = view;
		this.ifNotNullBlock = block;
	}

	public JExpression getView() {
		return view;
	}

	public JExpression getView(JClass viewClass) {
		if (this.viewClass.equals(viewClass) || holder.classes().VIEW.equals(viewClass)) {
			return view;
		} else {
			return cast(viewClass, view);
		}
	}

	public JBlock getIfNotNullBlock() {
		if (!ifNotNullCreated) {
			ifNotNullBlock = ifNotNullBlock._if(view.ne(_null()))._then();
			ifNotNullCreated = true;
		}
		return ifNotNullBlock;
	}
}
