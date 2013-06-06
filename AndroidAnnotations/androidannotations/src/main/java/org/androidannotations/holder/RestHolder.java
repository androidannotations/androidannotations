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
package org.androidannotations.holder;

import com.sun.codemodel.*;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.process.ProcessHolder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import java.util.List;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.androidannotations.helper.CanonicalNameConstants.REST_TEMPLATE;
import static org.androidannotations.helper.CanonicalNameConstants.STRING;

public class RestHolder extends BaseGeneratedClassHolder {

    private JMethod init;
	private JFieldVar rootUrlField;
	private JFieldVar restTemplateField;

	public RestHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		super(processHolder, annotatedElement);
		implementMethods();
	}

	@Override
	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		String subComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;
		JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);
		generatedClass = codeModel()._class(PUBLIC | FINAL, subComponentQualifiedName, ClassType.CLASS);
		generatedClass._implements(annotatedComponent);
	}

	private void implementMethods() {
		List<? extends Element> enclosedElements = annotatedElement.getEnclosedElements();
		List<ExecutableElement> methods = ElementFilter.methodsIn(enclosedElements);
		boolean getRestTemplateImplemented = false, setRestTemplateImplemented = false, getRootUrlImplemented = false, setRootUrlImplemented = false;
		for (ExecutableElement method : methods) {
			List<? extends VariableElement> parameters = method.getParameters();

			if (!getRestTemplateImplemented //
					&& method.getParameters().size() == 0 //
					&& method.getReturnType().toString().equals(REST_TEMPLATE)) {

				implementGetRestTemplateMethod(method);
				getRestTemplateImplemented = true;
			}

			if (!setRestTemplateImplemented //
					 && parameters.size() == 1 //
					 && parameters.get(0).asType().toString().equals(REST_TEMPLATE) //
					 && method.getReturnType().getKind() == TypeKind.VOID) {

				implementSetRestTemplateMethod(method);
				setRestTemplateImplemented = true;
			}

            if (!getRootUrlImplemented //
                    && parameters.size() == 0 //
                    && method.getReturnType().toString().equals(STRING) //
                    && method.getSimpleName().toString().equals("getRootUrl")) {
                implementGetRootUrlMethod(method);
                getRootUrlImplemented = true;
            }

			if (!setRootUrlImplemented //
					&& method.getSimpleName().toString().equals("setRootUrl")
					&& method.getReturnType().getKind() == TypeKind.VOID //
					&& parameters.size() == 1 //
					&& parameters.get(0).asType().toString().equals(STRING)) {

				implementSetRootUrl(method);
				setRootUrlImplemented = true;
			}
		}
	}

	private void implementGetRestTemplateMethod(ExecutableElement method) {
		String methodName = method.getSimpleName().toString();
		JMethod getRestTemplateMethod = getGeneratedClass().method(JMod.PUBLIC, classes().REST_TEMPLATE, methodName);
		getRestTemplateMethod.annotate(Override.class);
		getRestTemplateMethod.body()._return(getRestTemplateField());
	}

	private void implementSetRestTemplateMethod(ExecutableElement method) {
		String methodName = method.getSimpleName().toString();
		VariableElement firstParameter = method.getParameters().get(0);
		JMethod setRestTemplateMethod = getGeneratedClass().method(JMod.PUBLIC, codeModel().VOID, methodName);
		setRestTemplateMethod.annotate(Override.class);
		JVar restTemplateSetterParam = setRestTemplateMethod.param(classes().REST_TEMPLATE, firstParameter.getSimpleName().toString());
		setRestTemplateMethod.body().assign(_this().ref(getRestTemplateField()), restTemplateSetterParam);
	}

    private void implementGetRootUrlMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        JMethod getRootUrlMethod = getGeneratedClass().method(JMod.PUBLIC, processHolder.refClass(STRING), methodName);
        getRootUrlMethod.annotate(Override.class);
        getRootUrlMethod.body()._return(getRootUrlField());
    }

	private void implementSetRootUrl(ExecutableElement method) {
		String methodName = method.getSimpleName().toString();
		VariableElement firstParameter = method.getParameters().get(0);
		JMethod setRootUrlMethod = getGeneratedClass().method(JMod.PUBLIC, codeModel().VOID, methodName);
		setRootUrlMethod.annotate(Override.class);
		JVar rootUrlSetterParam = setRootUrlMethod.param(classes().STRING, firstParameter.getSimpleName().toString());
		setRootUrlMethod.body().assign(_this().ref(getRootUrlField()), rootUrlSetterParam);
	}

    public JMethod getInit() {
        if (init == null) {
            setInit();
        }
        return init;
    }

	private void setInit() {
		init = getGeneratedClass().constructor(JMod.PUBLIC);
	}

	public JFieldVar getRootUrlField() {
		if (rootUrlField == null) {
			setRootUrlField();
		}
		return rootUrlField;
	}

	private void setRootUrlField() {
		rootUrlField = getGeneratedClass().field(JMod.PRIVATE, classes().STRING, "rootUrl");
	}

	public JFieldVar getRestTemplateField() {
		if (restTemplateField == null) {
			setRestTemplateField();
		}
		return restTemplateField;
	}

	private void setRestTemplateField() {
		restTemplateField = getGeneratedClass().field(JMod.PRIVATE, classes().REST_TEMPLATE, "restTemplate");
		getInit().body().assign(restTemplateField, _new(classes().REST_TEMPLATE));
	}
}
