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

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.process.ProcessHolder;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JTypeVar;

public abstract class BaseGeneratedClassHolder implements GeneratedClassHolder {

	protected final ProcessHolder processHolder;
	protected JDefinedClass generatedClass;
	protected JClass annotatedClass;
	protected final TypeElement annotatedElement;
	protected final APTCodeModelHelper codeModelHelper;

	public BaseGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		this.processHolder = processHolder;
		this.annotatedElement = annotatedElement;
		codeModelHelper = new APTCodeModelHelper();
		setGeneratedClass();
	}

	protected void setGeneratedClass() throws Exception {
		String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
		annotatedClass = codeModel().directClass(annotatedElement.asType().toString());

		if (annotatedElement.getNestingKind().isNested()) {
			Element enclosingElement = annotatedElement.getEnclosingElement();
			GeneratedClassHolder enclosingHolder = processHolder.getGeneratedClassHolder(enclosingElement);
			String generatedBeanSimpleName = annotatedElement.getSimpleName().toString() + classSuffix();
			generatedClass = enclosingHolder.getGeneratedClass()._class(PUBLIC | FINAL | STATIC, generatedBeanSimpleName, ClassType.CLASS);
		} else {
			String generatedClassQualifiedName = annotatedComponentQualifiedName + classSuffix();
			generatedClass = codeModel()._class(PUBLIC | FINAL, generatedClassQualifiedName, ClassType.CLASS);
		}
		for (TypeParameterElement typeParam : annotatedElement.getTypeParameters()) {
			JClass bound = codeModelHelper.typeBoundsToJClass(this, typeParam.getBounds());
			generatedClass.generify(typeParam.getSimpleName().toString(), bound);
		}
		setExtends();
		codeModelHelper.addNonAAAnotations(generatedClass, annotatedElement.getAnnotationMirrors(), this);
	}

	public JClass getAnnotatedClass() {
		return annotatedClass;
	}

	protected void setExtends() {
		JClass annotatedComponent = codeModel().directClass(annotatedElement.asType().toString());
		generatedClass._extends(annotatedComponent);
	}

	@Override
	public JDefinedClass getGeneratedClass() {
		return generatedClass;
	}

	@Override
	public TypeElement getAnnotatedElement() {
		return annotatedElement;
	}

	@Override
	public ProcessingEnvironment processingEnvironment() {
		return processHolder.processingEnvironment();
	}

	@Override
	public ProcessHolder.Classes classes() {
		return processHolder.classes();
	}

	@Override
	public JCodeModel codeModel() {
		return processHolder.codeModel();
	}

	@Override
	public JClass refClass(String fullyQualifiedClassName) {
		return processHolder.refClass(fullyQualifiedClassName);
	}

	@Override
	public JClass refClass(Class<?> clazz) {
		return processHolder.refClass(clazz);
	}

	@Override
	public JDefinedClass definedClass(String fullyQualifiedClassName) {
		return processHolder.definedClass(fullyQualifiedClassName);
	}

	public JClass narrow(JClass toNarrow) {
		List<JClass> classes = new ArrayList<JClass>();
		for (JTypeVar type : generatedClass.typeParams()) {
			classes.add(codeModel().directClass(type.name()));
		}
		if (classes.isEmpty()) {
			return toNarrow;
		}
		return toNarrow.narrow(classes);
	}
}
