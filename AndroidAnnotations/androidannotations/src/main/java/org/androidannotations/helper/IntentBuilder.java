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
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;

import org.androidannotations.holder.HasIntentBuilder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import javax.lang.model.util.Elements;

public class IntentBuilder {

	protected HasIntentBuilder holder;
	protected JFieldVar contextField;
	protected JClass contextClass;
	protected JClass intentClass;
    protected JFieldVar fragmentField;
    protected JFieldVar fragmentSupportField;

    protected Elements elementUtils;

	public IntentBuilder(HasIntentBuilder holder) {
		this.holder = holder;
        elementUtils = holder.processingEnvironment().getElementUtils();
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
            JVar fragmentParam = method.param(holder.classes().SUPPORT_V4_FRAGMENT, "fragment");
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
        return elementUtils.getTypeElement(CanonicalNameConstants.FRAGMENT) != null;
    }

    private boolean hasFragmentSupportInClasspath() {
        return elementUtils.getTypeElement(CanonicalNameConstants.SUPPORT_V4_FRAGMENT) != null;
    }
}
