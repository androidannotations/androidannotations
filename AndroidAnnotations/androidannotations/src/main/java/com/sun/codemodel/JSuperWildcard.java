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
package com.sun.codemodel;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

// TODO : At his time (01/2013), it is not possible to handle the
// super bound because code model does not offer a way to model
// statement like " ? super X"
// (see http://java.net/jira/browse/CODEMODEL-11)
//
// This class is a hack against it.
// So, if it will be fixed in code model - just remove this
public class JSuperWildcard extends JClass {

	private final JClass bound;

	public JSuperWildcard(JClass bound) {
		super(bound.owner());
		this.bound = bound;
	}

	@Override
	public String name() {
		return "? super " + bound.name();
	}

	@Override
	public String fullName() {
		return "? super " + bound.fullName();
	}

	// CHECKSTYLE:OFF

	@Override
	public JPackage _package() {
		return null;
	}

	/**
	 * Returns the class bound of this variable.
	 *
	 * <p>
	 * If no bound is given, this method returns {@link Object}.
	 */
	@Override
	public JClass _extends() {
		if (bound != null) {
			return bound;
		} else {
			return owner().ref(Object.class);
		}
	}

	/**
	 * Returns the interface bounds of this variable.
	 */
	@Override
	public Iterator<JClass> _implements() {
		return bound._implements();
	}

	// CHECKSTYLE:ON

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
		// We should be able to use the following code with a direct call to
		// substituteParams(), because the class is in codemodel's package. But,
		// because of a possible Eclipse's issue on package visibility we have
		// to use reflection to make it works
		//
		// packages in
		// JClass nb = bound.substituteParams(variables, bindings);
		// if (nb == bound) {
		// return this;
		// } else {
		// return nb.wildcard();
		// }

		try {
			Method substituteParamsMethod = JClass.class.getDeclaredMethod("substituteParams", JTypeVar[].class, List.class);
			substituteParamsMethod.setAccessible(true);
			Object nb = substituteParamsMethod.invoke(bound, variables, bindings);
			if (nb == bound) {
				return this;
			} else {
				return ((JClass) nb).wildcard();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generate(JFormatter f) {
		if (bound._extends() == null) {
			f.p("?"); // instead of "? extends Object"
		} else {
			f.p("? super").g(bound);
		}
	}
}
