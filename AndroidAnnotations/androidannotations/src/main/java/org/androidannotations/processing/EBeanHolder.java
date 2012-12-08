/**
 * Copyright (C) 2010-2012 eBusiness Information, Excilys Group
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

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.lang.model.element.Element;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.processing.EBeansHolder.Classes;

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

	public final JDefinedClass generatedClass;
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

	public JFieldVar handler;

	public JBlock onOptionsItemSelectedIfElseBlock;
	public JVar onOptionsItemSelectedItemId;
	public JVar onOptionsItemSelectedItem;

	public JMethod restoreSavedInstanceStateMethod;
	public JBlock saveInstanceStateBlock;

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

	/**
	 * Only defined in activities
	 */
	public NonConfigurationHolder nonConfigurationHolder;

	/**
	 * TextWatchers by idRef
	 */
	public final HashMap<String, TextWatcherHolder> textWatchers = new HashMap<String, TextWatcherHolder>();

	/**
	 * OnActivityResult byResultCode
	 */
	public final HashMap<Integer, JBlock> onActivityResultCases = new HashMap<Integer, JBlock>();

	public JSwitch onActivityResultSwitch;
	public JMethod onActivityResultMethod;

	/**
	 * onSeekBarChangeListeners by idRef
	 */
	public final HashMap<String, OnSeekBarChangeListenerHolder> onSeekBarChangeListeners = new HashMap<String, OnSeekBarChangeListenerHolder>();

	public JVar fragmentArguments;
	public JFieldVar fragmentArgumentsBuilderField;
	public JMethod fragmentArgumentsInjectMethod;
	public JBlock fragmentArgumentsNotNullBlock;
	public JDefinedClass fragmentBuilderClass;

	public JMethod findNativeFragmentById;
	public JMethod findSupportFragmentById;
	public JMethod findNativeFragmentByTag;
	public JMethod findSupportFragmentByTag;

	private final EBeansHolder eBeansHolder;
	public final Class<? extends Annotation> eBeanAnnotation;

	public EBeanHolder(EBeansHolder eBeansHolder, Class<? extends Annotation> eBeanAnnotation, JDefinedClass generatedClass) {
		this.eBeansHolder = eBeansHolder;
		this.eBeanAnnotation = eBeanAnnotation;
		this.generatedClass = generatedClass;
	}

	public Classes classes() {
		return eBeansHolder.classes();
	}

	public JCodeModel codeModel() {
		return eBeansHolder.codeModel();
	}

	public JClass refClass(String fullyQualifiedClassName) {
		return eBeansHolder.refClass(fullyQualifiedClassName);
	}

	public JClass refClass(Class<?> clazz) {
		return eBeansHolder.refClass(clazz);
	}

	public void generateApiClass(Element originatingElement, Class<?> apiClass) {
		eBeansHolder.generateApiClass(originatingElement, apiClass);
	}

}
