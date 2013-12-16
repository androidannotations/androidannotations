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
package org.androidannotations.helper;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.SERIALIZABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class IntentBuilder {

	private static final int MIN_SDK_WITH_FRAGMENT_SUPPORT = 11;

	protected HasIntentBuilder holder;
	protected AndroidManifest androidManifest;
	protected JFieldVar contextField;
	protected JClass contextClass;
	protected JClass intentClass;
	protected JFieldVar fragmentField;
	protected JFieldVar fragmentSupportField;
	protected Map<Pair<TypeMirror, String>, JMethod> putExtraMethods = new HashMap<Pair<TypeMirror, String>, JMethod>();

	protected Elements elementUtils;
	protected Types typeUtils;
	protected APTCodeModelHelper codeModelHelper = new APTCodeModelHelper();

	public IntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		this.holder = holder;
		this.androidManifest = androidManifest;
		elementUtils = holder.processingEnvironment().getElementUtils();
		typeUtils = holder.processingEnvironment().getTypeUtils();
		contextClass = holder.classes().CONTEXT;
		intentClass = holder.classes().INTENT;
	}

	public void build() throws JClassAlreadyExistsException {
		createClass();
		createConstructor();
		createAdditionalConstructor(); // See issue #541
		createGet();
		createFlags();
		createIntent();
	}

	private void createClass() throws JClassAlreadyExistsException {
		holder.setIntentBuilderClass(holder.getGeneratedClass()._class(PUBLIC | STATIC, "IntentBuilder_"));
		contextField = holder.getIntentBuilderClass().field(PRIVATE, contextClass, "context_");
		holder.setIntentField(holder.getIntentBuilderClass().field(PRIVATE | FINAL, intentClass, "intent_"));
	}

	private void createConstructor() {
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorContextParam = constructor.param(contextClass, "context");
		JBlock constructorBody = constructor.body();
		constructorBody.assign(contextField, constructorContextParam);
		constructorBody.assign(holder.getIntentField(), _new(intentClass).arg(constructorContextParam).arg(holder.getGeneratedClass().dotclass()));
	}

	private void createAdditionalConstructor() {
		if (hasFragmentInClasspath()) {
			fragmentField = addFragmentConstructor(holder.classes().FRAGMENT, "fragment_");
		}
		if (hasFragmentSupportInClasspath()) {
			fragmentSupportField = addFragmentConstructor(holder.classes().SUPPORT_V4_FRAGMENT, "fragmentSupport_");
		}
	}

	private void createGet() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, intentClass, "get");
		method.body()._return(holder.getIntentField());
	}

	private void createFlags() {
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), "flags");
		JVar flagsParam = method.param(holder.codeModel().INT, "flags");
		JBlock body = method.body();
		body.invoke(holder.getIntentField(), "setFlags").arg(flagsParam);
		body._return(_this());
	}

	private void createIntent() {
		JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
		JVar contextParam = method.param(contextClass, "context");
		method.body()._return(_new(holder.getIntentBuilderClass()).arg(contextParam));

		if (hasFragmentInClasspath()) {
			// intent() with android.app.Fragment param
			method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().FRAGMENT, "fragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
		if (hasFragmentSupportInClasspath()) {
			// intent() with android.support.v4.app.Fragment param
			method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().SUPPORT_V4_FRAGMENT, "supportFragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
	}

	private JFieldVar addFragmentConstructor(JClass fragmentClass, String fieldName) {
		JFieldVar fragmentField = holder.getIntentBuilderClass().field(PRIVATE, fragmentClass, fieldName);
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorFragmentParam = constructor.param(fragmentClass, "fragment");
		JBlock constructorBody = constructor.body();
		constructorBody.assign(fragmentField, constructorFragmentParam);
		constructorBody.assign(contextField, constructorFragmentParam.invoke("getActivity"));
		constructorBody.assign(holder.getIntentField(), _new(holder.classes().INTENT).arg(contextField).arg(holder.getGeneratedClass().dotclass()));
		return fragmentField;
	}

	private boolean hasFragmentInClasspath() {
		boolean fragmentExistsInSdk = androidManifest.getMinSdkVersion() >= MIN_SDK_WITH_FRAGMENT_SUPPORT;
		return fragmentExistsInSdk && elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT) != null;
	}

	private boolean hasFragmentSupportInClasspath() {
		return elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT) != null;
	}

	public JMethod getPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField) {
		Pair<TypeMirror, String> signature = new Pair<TypeMirror, String>(elementType, parameterName);
		JMethod putExtraMethod = putExtraMethods.get(signature);
		if (putExtraMethod == null) {
			putExtraMethod = addPutExtraMethod(elementType, parameterName, extraKeyField);
			putExtraMethods.put(signature, putExtraMethod);
		}
		return putExtraMethod;
	}

	private JMethod addPutExtraMethod(TypeMirror elementType, String parameterName, JFieldVar extraKeyField) {
		boolean castToSerializable = false;
		boolean castToParcelable = false;
		if (elementType.getKind() == TypeKind.DECLARED) {
			Elements elementUtils = holder.processingEnvironment().getElementUtils();
			TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
			if (!typeUtils.isSubtype(elementType, parcelableType)) {
				TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
				if (!typeUtils.isSubtype(elementType, stringType)) {
					castToSerializable = true;
				}
			} else {
				TypeMirror serializableType = elementUtils.getTypeElement(SERIALIZABLE).asType();
				if (typeUtils.isSubtype(elementType, serializableType)) {
					castToParcelable = true;
				}
			}
		}

		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), parameterName);
		JClass parameterClass = codeModelHelper.typeMirrorToJClass(elementType, holder);
		JVar extraParameterVar = method.param(parameterClass, parameterName);
		JBlock body = method.body();
		JInvocation invocation = body.invoke(holder.getIntentField(), "putExtra").arg(extraKeyField);
		if (castToSerializable) {
			invocation.arg(cast(holder.classes().SERIALIZABLE, extraParameterVar));
		} else if (castToParcelable) {
			invocation.arg(cast(holder.classes().PARCELABLE, extraParameterVar));
		} else {
			invocation.arg(extraParameterVar);
		}
		body._return(_this());
		return method;
	}

}
