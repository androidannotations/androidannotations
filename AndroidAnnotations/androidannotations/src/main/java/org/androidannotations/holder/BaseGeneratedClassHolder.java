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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PUBLIC;

public abstract class BaseGeneratedClassHolder implements GeneratedClassHolder {

    protected final ProcessHolder processHolder;
    protected JDefinedClass generatedClass;
    protected final TypeElement annotatedElement;

    public BaseGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
        this.processHolder = processHolder;
        this.annotatedElement = annotatedElement;
        setGeneratedClass();
    }

    protected void setGeneratedClass() throws Exception {
        String annotatedComponentQualifiedName = annotatedElement.getQualifiedName().toString();
        String subComponentQualifiedName = annotatedComponentQualifiedName + ModelConstants.GENERATION_SUFFIX;
        JClass annotatedComponent = codeModel().directClass(annotatedComponentQualifiedName);
        generatedClass = codeModel()._class(PUBLIC | FINAL, subComponentQualifiedName, ClassType.CLASS);
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

    @Override
    public void generateApiClass(Element originatingElement, Class<?> apiClass) {
        processHolder.generateApiClass(originatingElement, apiClass);
    }
}
