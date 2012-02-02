/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JVar;

public class EBeanHolder {

	public JDefinedClass eBean;
	/**
	 * Only defined on activities
	 */
	public JVar beforeCreateSavedInstanceStateParam;
	public JMethod init;
	/**
	 * Only defined on activities and components potentially depending on
	 * activity ( {@link EViewGroup}, {@link EBean}
	 */
	public JMethod afterSetContentView;
	public JBlock extrasNotNullBlock;
	public JVar extras;
	public JVar resources;

	public JMethod cast;

	private Map<String, JClass> loadedClasses = new HashMap<String, JClass>();
	public JFieldVar handler;

	public JSwitch onOptionsItemSelectedSwitch;
	public JVar onOptionsItemSelectedItem;

	public JExpression contextRef;
	/**
	 * Should not be used by inner annotations that target services, broadcast
	 * receivers, and content providers
	 */
	public JBlock initIfActivityBody;
	public JExpression initActivityRef;

	/**
	 * Only defined in activities
	 */
	public JDefinedClass intentBuilderClass;

	/**
	 * Only defined in activities
	 */
	public JFieldVar intentField;

	public JClass refClass(String fullyQualifiedClassName) {

		JClass refClass = loadedClasses.get(fullyQualifiedClassName);

		if (refClass == null) {
			JCodeModel codeModel = eBean.owner();
			try {
				refClass = codeModel.ref(fullyQualifiedClassName);
			} catch (Exception e) {
				refClass = codeModel.directClass(fullyQualifiedClassName);
			}
			loadedClasses.put(fullyQualifiedClassName, refClass);
		}

		return refClass;
	}

	public JClass refClass(Class<?> clazz) {
		return eBean.owner().ref(clazz);
	}

}
