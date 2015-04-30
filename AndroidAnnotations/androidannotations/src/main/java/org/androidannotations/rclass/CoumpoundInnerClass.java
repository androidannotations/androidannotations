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
package org.androidannotations.rclass;

import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.JFieldRef;

public class CoumpoundInnerClass implements IRInnerClass {

	private final IRInnerClass rInnerClass;
	private final IRInnerClass androidRInnerClass;

	public CoumpoundInnerClass(IRInnerClass rInnerClass, IRInnerClass androidRInnerClass) {
		this.rInnerClass = rInnerClass;
		this.androidRInnerClass = androidRInnerClass;
	}

	@Override
	public boolean containsIdValue(Integer idValue) {
		return rInnerClass.containsIdValue(idValue) || androidRInnerClass.containsIdValue(idValue);
	}

	@Override
	public String getIdQualifiedName(Integer idValue) {
		String idQualifiedName = rInnerClass.getIdQualifiedName(idValue);
		if (idQualifiedName == null) {
			idQualifiedName = androidRInnerClass.getIdQualifiedName(idValue);
		}
		return idQualifiedName;
	}

	@Override
	public boolean containsField(String name) {
		return rInnerClass.containsField(name) || androidRInnerClass.containsField(name);
	}

	@Override
	public String getIdQualifiedName(String name) {
		String idQualifiedName = rInnerClass.getIdQualifiedName(name);
		if (idQualifiedName == null) {
			idQualifiedName = androidRInnerClass.getIdQualifiedName(name);
		}
		return idQualifiedName;
	}

	@Override
	public JFieldRef getIdStaticRef(Integer idValue, ProcessHolder holder) {
		JFieldRef idStaticRef = rInnerClass.getIdStaticRef(idValue, holder);
		if (idStaticRef == null) {
			idStaticRef = androidRInnerClass.getIdStaticRef(idValue, holder);
		}
		return idStaticRef;
	}

	@Override
	public JFieldRef getIdStaticRef(String name, ProcessHolder holder) {
		JFieldRef idStaticRef = rInnerClass.getIdStaticRef(name, holder);
		if (idStaticRef == null) {
			idStaticRef = androidRInnerClass.getIdStaticRef(name, holder);
		}
		return idStaticRef;
	}
}
