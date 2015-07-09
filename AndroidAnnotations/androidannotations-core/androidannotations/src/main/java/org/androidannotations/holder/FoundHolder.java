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

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;

public abstract class FoundHolder {

	private GeneratedClassHolder holder;
	private JClass type;
	private JExpression ref;
	private JBlock ifNotNullBlock;

	private boolean ifNotNullCreated = false;

	public FoundHolder(GeneratedClassHolder holder, JClass type, JExpression ref, JBlock block) {
		this.holder = holder;
		this.type = type;
		this.ref = ref;
		ifNotNullBlock = block;
	}

	public GeneratedClassHolder getGeneratedClassHolder() {
		return holder;
	}

	public JExpression getRef() {
		return ref;
	}

	public JExpression getOrCastRef(JClass type) {
		if (this.type.equals(type) || getBaseType().equals(type)) {
			return ref;
		} else {
			return cast(type, ref);
		}
	}

	protected abstract JClass getBaseType();

	public JBlock getIfNotNullBlock() {
		if (!ifNotNullCreated) {
			ifNotNullBlock = ifNotNullBlock._if(ref.ne(_null()))._then();
			ifNotNullCreated = true;
		}
		return ifNotNullBlock;
	}
}
