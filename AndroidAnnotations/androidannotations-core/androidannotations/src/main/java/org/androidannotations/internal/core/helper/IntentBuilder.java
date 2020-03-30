/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
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
package org.androidannotations.internal.core.helper;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.ref;
import static com.helger.jcodemodel.JMod.PUBLIC;
import static com.helger.jcodemodel.JMod.STATIC;
import static org.androidannotations.helper.CanonicalNameConstants.PARCELABLE;
import static org.androidannotations.helper.CanonicalNameConstants.SERIALIZABLE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.ParcelerHelper;
import org.androidannotations.holder.HasIntentBuilder;
import org.androidannotations.internal.process.ProcessHolder;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public abstract class IntentBuilder {

	protected AndroidAnnotationsEnvironment environment;
	protected HasIntentBuilder holder;
	protected AndroidManifest androidManifest;
	protected JDefinedClass builderClass;
	protected JFieldRef contextField;
	protected JFieldRef intentField;
	protected AbstractJClass contextClass;
	protected AbstractJClass intentClass;

	protected Elements elementUtils;
	protected Types typeUtils;
	protected APTCodeModelHelper codeModelHelper;
	protected AnnotationHelper annotationHelper;
	protected ParcelerHelper parcelerHelper;

	public IntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		this.environment = holder.getEnvironment();
		this.holder = holder;
		this.androidManifest = androidManifest;
		this.annotationHelper = new AnnotationHelper(environment);
		this.parcelerHelper = new ParcelerHelper(environment);
		codeModelHelper = new APTCodeModelHelper(environment);
		elementUtils = environment.getProcessingEnvironment().getElementUtils();
		typeUtils = environment.getProcessingEnvironment().getTypeUtils();
		contextClass = environment.getClasses().CONTEXT;
		intentClass = environment.getClasses().INTENT;
	}

	public void build() throws JClassAlreadyExistsException {
		createClass();
		createContextConstructor();
		createIntentMethod();
	}

	private void createClass() throws JClassAlreadyExistsException {
		builderClass = holder.getGeneratedClass()._class(PUBLIC | STATIC, "IntentBuilder" + generationSuffix());
		builderClass._extends(getSuperClass());
		holder.setIntentBuilderClass(builderClass);
		contextField = ref("context");
		intentField = ref("intent");
	}

	private void createContextConstructor() {
		IJExpression generatedClass = holder.getGeneratedClass().dotclass();
		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorContextParam = constructor.param(getClasses().CONTEXT, "context");
		constructor.body().invoke("super").arg(constructorContextParam).arg(generatedClass);
	}

	private void createIntentMethod() {
		JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
		JVar contextParam = method.param(contextClass, "context");
		method.body()._return(_new(holder.getIntentBuilderClass()).arg(contextParam));
	}

	public JMethod getPutExtraMethod(Element element, IntentExtra intentExtra) {
		return addPutExtraMethod(element, Collections.singletonList(intentExtra));
	}

	public JMethod getPutExtraMethod(Element element, List<IntentExtra> intentExtra) {
		return addPutExtraMethod(element, intentExtra);
	}

	private JMethod addPutExtraMethod(Element element, List<IntentExtra> intentExtras) {
		String docComment = elementUtils.getDocComment(element);

		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.getIntentBuilderClass(), element.getSimpleName().toString());
		method.javadoc().addReturn().append("the IntentBuilder to chain calls");
		codeModelHelper.addTrimmedDocComment(method, docComment);

		int paramCount = intentExtras.size();
		for (int i = 0; i < paramCount; i++) {
			IntentExtra intentExtra = intentExtras.get(i);
			method.javadoc().addParam(intentExtra.parameterName).append("the value for this extra");
			AbstractJClass parameterClass = codeModelHelper.typeMirrorToJClass(intentExtra.type);
			JVar extraParameterVar = method.param(parameterClass, intentExtra.parameterName);
			JInvocation superCall = getSuperPutExtraInvocation(intentExtra.type, extraParameterVar, intentExtra.keyField);
			if (i + 1 == paramCount) {
				method.body()._return(superCall);
			} else {
				method.body().add(superCall);
			}
		}
		return method;
	}

	public JInvocation getSuperPutExtraInvocation(TypeMirror elementType, JVar extraParam, JFieldVar extraKeyField) {
		IJExpression extraParameterArg = extraParam;
		// Cast to Parcelable, wrap with Parcels.wrap or cast Serializable if needed
		if (elementType.getKind() == TypeKind.DECLARED) {
			TypeMirror parcelableType = elementUtils.getTypeElement(PARCELABLE).asType();
			if (typeUtils.isSubtype(elementType, parcelableType)) {
				TypeMirror serializableType = elementUtils.getTypeElement(SERIALIZABLE).asType();
				if (typeUtils.isSubtype(elementType, serializableType)) {
					extraParameterArg = cast(environment.getClasses().PARCELABLE, extraParameterArg);
				}
			} else if (!BundleHelper.METHOD_SUFFIX_BY_TYPE_NAME.containsKey(elementType.toString()) && parcelerHelper.isParcelType(elementType)) {
				extraParameterArg = environment.getJClass(CanonicalNameConstants.PARCELS_UTILITY_CLASS).staticInvoke("wrap").arg(extraParameterArg);
			} else {
				TypeMirror stringType = elementUtils.getTypeElement(STRING).asType();
				if (!typeUtils.isSubtype(elementType, stringType)) {
					extraParameterArg = cast(environment.getClasses().SERIALIZABLE, extraParameterArg);
				}
			}
		}
		return _super().invoke("extra").arg(extraKeyField).arg(extraParameterArg);
	}

	protected abstract AbstractJClass getSuperClass();

	protected ProcessHolder.Classes getClasses() {
		return environment.getClasses();
	}

	protected AbstractJClass getJClass(Class<?> clazz) {
		return environment.getJClass(clazz);
	}

	public static class IntentExtra {
		private final TypeMirror type;
		private final String parameterName;
		private final JFieldVar keyField;

		public IntentExtra(TypeMirror type, String parameterName, JFieldVar keyField) {
			this.type = type;
			this.parameterName = parameterName;
			this.keyField = keyField;
		}

		public TypeMirror getType() {
			return type;
		}

		public String getParameterName() {
			return parameterName;
		}

		public JFieldVar getKeyField() {
			return keyField;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			IntentExtra that = (IntentExtra) o;
			return Objects.equals(type, that.type) && Objects.equals(parameterName, that.parameterName) && Objects.equals(keyField, that.keyField);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, parameterName, keyField);
		}
	}
}
