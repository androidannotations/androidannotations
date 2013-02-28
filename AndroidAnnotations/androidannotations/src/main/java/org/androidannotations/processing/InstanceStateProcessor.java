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
package org.androidannotations.processing;

import static org.androidannotations.helper.CanonicalNameConstants.BUNDLE;
import static org.androidannotations.helper.CanonicalNameConstants.CHAR_SEQUENCE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import org.androidannotations.annotations.InstanceState;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.BundleHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class InstanceStateProcessor implements DecoratingElementProcessor {

	private static final String BUNDLE_PARAM_NAME = "bundle";

	public static final Map<String, String> methodSuffixNameByTypeName = new HashMap<String, String>();

	static {

		methodSuffixNameByTypeName.put(BUNDLE, "Bundle");

		methodSuffixNameByTypeName.put("boolean", "Boolean");
		methodSuffixNameByTypeName.put("boolean[]", "BooleanArray");

		methodSuffixNameByTypeName.put("byte", "Byte");
		methodSuffixNameByTypeName.put("byte[]", "ByteArray");

		methodSuffixNameByTypeName.put("char", "Char");
		methodSuffixNameByTypeName.put("char[]", "CharArray");

		methodSuffixNameByTypeName.put(CHAR_SEQUENCE, "CharSequence");

		methodSuffixNameByTypeName.put("double", "Double");
		methodSuffixNameByTypeName.put("double[]", "DoubleArray");

		methodSuffixNameByTypeName.put("float", "Float");
		methodSuffixNameByTypeName.put("float[]", "FloatArray");

		methodSuffixNameByTypeName.put("int", "Int");
		methodSuffixNameByTypeName.put("int[]", "IntArray");
		methodSuffixNameByTypeName.put("java.util.ArrayList<java.lang.Integer>", "IntegerArrayList");

		methodSuffixNameByTypeName.put("long", "Long");
		methodSuffixNameByTypeName.put("long[]", "LongArray");

		methodSuffixNameByTypeName.put("short", "Short");
		methodSuffixNameByTypeName.put("short[]", "ShortArray");

		methodSuffixNameByTypeName.put(STRING, "String");
		methodSuffixNameByTypeName.put("java.lang.String[]", "StringArray");
		methodSuffixNameByTypeName.put("java.util.ArrayList<java.lang.String>", "StringArrayList");
	}

	private final APTCodeModelHelper helper = new APTCodeModelHelper();

	private AnnotationHelper annotationHelper;

	public InstanceStateProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return InstanceState.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		String fieldName = element.getSimpleName().toString();

		JBlock saveStateBody = getSaveStateMethodBody(codeModel, holder);
		JBlock restoreStateBody = getRestoreStateBody(codeModel, holder);

		BundleHelper bundleHelper = new BundleHelper(annotationHelper, element);

		JFieldRef ref = ref(fieldName);
		saveStateBody.invoke(ref(BUNDLE_PARAM_NAME), bundleHelper.getMethodNameToSave()).arg(fieldName).arg(ref);

		JInvocation restoreMethodCall = JExpr.invoke(ref("savedInstanceState"), bundleHelper.getMethodNameToRestore()).arg(fieldName);
		if (bundleHelper.restoreCallNeedCastStatement()) {

			JClass jclass = helper.typeMirrorToJClass(element.asType(), holder);
			JExpression castStatement = JExpr.cast(jclass, restoreMethodCall);
			restoreStateBody.assign(ref, castStatement);

			if (bundleHelper.restoreCallNeedsSuppressWarning()) {
				if (holder.restoreSavedInstanceStateMethod.annotations().size() == 0) {
					holder.restoreSavedInstanceStateMethod.annotate(SuppressWarnings.class).param("value", "unchecked");
				}
			}

		} else {
			restoreStateBody.assign(ref, restoreMethodCall);
		}
	}

	private JBlock getRestoreStateBody(JCodeModel codeModel, EBeanHolder holder) {

		if (holder.restoreSavedInstanceStateMethod == null) {

			holder.restoreSavedInstanceStateMethod = holder.generatedClass.method(PRIVATE, codeModel.VOID, "restoreSavedInstanceState_");

			JVar savedInstanceState = holder.restoreSavedInstanceStateMethod.param(holder.classes().BUNDLE, "savedInstanceState");

			holder.initIfActivityBody.invoke(holder.restoreSavedInstanceStateMethod).arg(savedInstanceState);

			holder.restoreSavedInstanceStateMethod.body() //
					._if(ref("savedInstanceState").eq(_null())) //
					._then()._return();

		}

		return holder.restoreSavedInstanceStateMethod.body();
	}

	private JBlock getSaveStateMethodBody(JCodeModel codeModel, EBeanHolder holder) {

		if (holder.saveInstanceStateBlock == null) {
			JMethod method = holder.generatedClass.method(PUBLIC, codeModel.VOID, "onSaveInstanceState");
			method.annotate(Override.class);
			method.param(holder.classes().BUNDLE, BUNDLE_PARAM_NAME);

			holder.saveInstanceStateBlock = method.body();

			holder.saveInstanceStateBlock.invoke(JExpr._super(), "onSaveInstanceState").arg(JExpr.ref(BUNDLE_PARAM_NAME));
		}

		return holder.saveInstanceStateBlock;
	}

}
