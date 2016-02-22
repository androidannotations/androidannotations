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
package org.androidannotations.holder;

import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr.cast;

import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;

public abstract class FoundHolder {

	private GeneratedClassHolder holder;
	private AbstractJClass type;
	private IJExpression ref;
	private JBlock ifNotNullBlock;

	private boolean ifNotNullCreated = false;

	public FoundHolder(GeneratedClassHolder holder, AbstractJClass type, IJExpression ref, JBlock block) {
		this.holder = holder;
		this.type = type;
		this.ref = ref;
		ifNotNullBlock = block;
	}

	public GeneratedClassHolder getGeneratedClassHolder() {
		return holder;
	}

	public IJExpression getRef() {
		return ref;
	}

	public IJExpression getOrCastRef(AbstractJClass type) {
		if (this.type.equals(type) || getBaseType().equals(type)) {
			return ref;
		} else {
			return cast(type, ref);
		}
	}

	protected abstract AbstractJClass getBaseType();

	public JBlock getIfNotNullBlock() {
		if (!ifNotNullCreated) {
			ifNotNullBlock = ifNotNullBlock._if(ref.ne(_null()))._then();
			ifNotNullCreated = true;
		}
		return ifNotNullBlock;
	}

	protected ProcessHolder.Classes getClasses() {
		return holder.getEnvironment().getClasses();
	}
}
