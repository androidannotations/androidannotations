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

import static com.sun.codemodel.JMod.PUBLIC;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

public class NonConfigurationHolder {

	private JDefinedClass generatedClass;
	private JFieldVar superNonConfigurationInstanceField;

	public NonConfigurationHolder(EActivityHolder eActivityHolder) throws JClassAlreadyExistsException {
		setGeneratedClass(eActivityHolder);
	}

	private void setGeneratedClass(EActivityHolder eActivityHolder) throws JClassAlreadyExistsException {
		generatedClass = eActivityHolder.generatedClass._class(JMod.PRIVATE | JMod.STATIC, "NonConfigurationInstancesHolder");
	}

	public JDefinedClass getGeneratedClass() {
		return generatedClass;
	}

	public JFieldVar getSuperNonConfigurationInstanceField() {
		if (superNonConfigurationInstanceField == null) {
			setSuperNonConfigurationInstanceField();
		}
		return superNonConfigurationInstanceField;
	}

	private void setSuperNonConfigurationInstanceField() {
		superNonConfigurationInstanceField = generatedClass.field(PUBLIC, Object.class, "superNonConfigurationInstance");
	}

	public JFieldVar createField(String fieldName, JClass fieldType) {
		return generatedClass.field(PUBLIC, fieldType, fieldName);
	}
}
