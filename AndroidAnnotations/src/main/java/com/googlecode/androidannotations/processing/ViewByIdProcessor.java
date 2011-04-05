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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class ViewByIdProcessor implements ElementProcessor {

	private final IRClass rClass;

	public ViewByIdProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ViewById.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, ActivitiesHolder activitiesHolder) {
	    
	    ActivityHolder holder = activitiesHolder.getEnclosingActivityHolder(element);
	    
        String fieldName = element.getSimpleName().toString();

        TypeMirror uiFieldTypeMirror = element.asType();
        String typeQualifiedName = uiFieldTypeMirror.toString();

        ViewById annotation = element.getAnnotation(ViewById.class);
        int idValue = annotation.value();

        IRInnerClass rInnerClass = rClass.get(Res.ID);
        JFieldRef idRef;
        if (idValue == Id.DEFAULT_VALUE) {
            idRef = rInnerClass.getIdStaticRef(fieldName, holder);
        } else {
            idRef = rInnerClass.getIdStaticRef(idValue, holder);
        }
        
        JBlock methodBody = holder.afterSetContentView.body();
	    
        methodBody.assign(JExpr.ref(fieldName), JExpr.cast(holder.refClass(typeQualifiedName), JExpr.invoke("findViewById").arg(idRef)));
	}

}
