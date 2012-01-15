/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.validation;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.EAppWidgetProvider;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.googlecode.androidannotations.helper.ValidatorHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class EAppWidgetProviderValidator implements ElementValidator {

    private TargetAnnotationHelper annotationHelper;
    private ValidatorHelper validatorHelper;

    public EAppWidgetProviderValidator(ProcessingEnvironment processingEnv) {
        annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
        validatorHelper = new ValidatorHelper(annotationHelper);
    }

    @Override
    public Class<? extends Annotation> getTarget() {
        return EAppWidgetProvider.class;
    }

    @Override
    public boolean validate(Element element, AnnotationElements validatedElements) {

        IsValid valid = new IsValid();

        validatorHelper.extendsAppWidgetProvider(element, valid);

        validatorHelper.isNotFinal(element, valid);

        return valid.isValid();
    }
}
