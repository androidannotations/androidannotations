/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
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

import com.sun.codemodel.*;
import org.androidannotations.holder.HasIntentBuilder;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

public class ActivityIntentBuilder extends IntentBuilder {

	private static final int MIN_SDK_WITH_FRAGMENT_SUPPORT = 11;

	private JFieldVar fragmentField;
	private JFieldVar fragmentSupportField;

	public ActivityIntentBuilder(HasIntentBuilder holder, AndroidManifest androidManifest) {
		super(holder, androidManifest);
	}

	@Override
	public void build() throws JClassAlreadyExistsException {
		super.build();
		createAdditionalConstructor(); // See issue #541
		createAdditionalIntentMethods();
		overrideStartForResultMethod();
	}

	private void createAdditionalIntentMethods() {
		if (hasFragmentInClasspath()) {
			// intent() with android.app.Fragment param
			JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().FRAGMENT, "fragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
		if (hasFragmentSupportInClasspath()) {
			// intent() with android.support.v4.app.Fragment param
			JMethod method = holder.getGeneratedClass().method(STATIC | PUBLIC, holder.getIntentBuilderClass(), "intent");
			JVar fragmentParam = method.param(holder.classes().SUPPORT_V4_FRAGMENT, "supportFragment");
			method.body()._return(_new(holder.getIntentBuilderClass()).arg(fragmentParam));
		}
	}

	@Override
	protected JClass getSuperClass() {
		JClass superClass = holder.refClass(org.androidannotations.api.builder.ActivityIntentBuilder.class);
		return superClass.narrow(builderClass);
	}

	private void createAdditionalConstructor() {
		if (hasFragmentInClasspath()) {
			fragmentField = addFragmentConstructor(holder.classes().FRAGMENT, "fragment_");
		}
		if (hasFragmentSupportInClasspath()) {
			fragmentSupportField = addFragmentConstructor(holder.classes().SUPPORT_V4_FRAGMENT, "fragmentSupport_");
		}
	}

	private JFieldVar addFragmentConstructor(JClass fragmentClass, String fieldName) {
		JFieldVar fragmentField = holder.getIntentBuilderClass().field(PRIVATE, fragmentClass, fieldName);
		JExpression generatedClass = holder.getGeneratedClass().dotclass();

		JMethod constructor = holder.getIntentBuilderClass().constructor(JMod.PUBLIC);
		JVar constructorFragmentParam = constructor.param(fragmentClass, "fragment");
		JBlock constructorBody = constructor.body();
		constructorBody.invoke("super").arg(constructorFragmentParam.invoke("getActivity")).arg(generatedClass);
		constructorBody.assign(fragmentField, constructorFragmentParam);

		return fragmentField;
	}

	private void overrideStartForResultMethod() {
		if (fragmentSupportField == null && fragmentField == null) {
			return;
		}
		JMethod method = holder.getIntentBuilderClass().method(PUBLIC, holder.codeModel().VOID, "startForResult");
		method.annotate(Override.class);
		JVar requestCode = method.param(holder.codeModel().INT, "requestCode");
		JBlock body = method.body();

		JConditional condition = null;
		if (fragmentSupportField != null) {
			condition = body._if(fragmentSupportField.ne(JExpr._null()));
			condition._then() //
					.invoke(fragmentSupportField, "startActivityForResult").arg(intentField).arg(requestCode);
		}
		if (fragmentField != null) {
			if (condition == null) {
				condition = body._if(fragmentField.ne(JExpr._null()));
			} else {
				condition = condition._elseif(fragmentField.ne(JExpr._null()));
			}
			condition._then() //
					.invoke(fragmentField, "startActivityForResult").arg(intentField).arg(requestCode);
		}
		condition._else().invoke(_super(), "startForResult").arg(requestCode);
	}

	protected boolean hasFragmentInClasspath() {
		boolean fragmentExistsInSdk = androidManifest.getMinSdkVersion() >= MIN_SDK_WITH_FRAGMENT_SUPPORT;
		return fragmentExistsInSdk && elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT) != null;
	}

	protected boolean hasFragmentSupportInClasspath() {
		return elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT) != null;
	}
}
