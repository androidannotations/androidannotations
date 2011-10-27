/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class ClickProcessor implements ElementProcessor {

	private final IRClass rClass;

	public ClickProcessor(IRClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Click.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {

		EBeanHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean hasViewParameter = parameters.size() == 1;

		List<JFieldRef> idRefs = extractClickQualifiedIds(element, holder);

		JDefinedClass onClickListenerClass = codeModel.anonymousClass(holder.refClass("android.view.View.OnClickListener"));
		JMethod onClickMethod = onClickListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onClick");
		JClass viewClass = holder.refClass("android.view.View");
		JVar onClickViewParam = onClickMethod.param(viewClass, "view");

		JInvocation clickCall = onClickMethod.body().invoke(methodName);

		if (hasViewParameter) {
			clickCall.arg(onClickViewParam);
		}

		JBlock block = holder.afterSetContentView.body().block();
		        
        for (int i = 0 ; i < idRefs.size() ; i++) {
        	JFieldRef idRef = idRefs.get(i);

            JInvocation findViewById = JExpr.invoke("findViewById");
        	JVar view = block.decl(viewClass, "view" + i, findViewById.arg(idRef));
        	block._if(view.ne(JExpr._null()))._then().invoke(view, "setOnClickListener").arg(JExpr._new(onClickListenerClass));
        }
		
	}

	private List<JFieldRef> extractClickQualifiedIds(Element element, EBeanHolder holder) {
		
		List<JFieldRef> idRefs = new ArrayList<JFieldRef>();
		Click annotation = element.getAnnotation(Click.class);
		
		int idValue = annotation.value();
		int [] idsValues = annotation.ids();
		IRInnerClass rInnerClass = rClass.get(Res.ID);
		
		if (idsValues.length != 0) {
			
			for(int id : idsValues) {
				JFieldRef idRef = rInnerClass.getIdStaticRef(id, holder);
				idRefs.add(idRef);
			}
			
		} else if (idValue == Id.DEFAULT_VALUE) {
			
			String fieldName = element.getSimpleName().toString();
			int lastIndex = fieldName.lastIndexOf("Clicked");
			
			if (lastIndex != -1) {
				fieldName = fieldName.substring(0, lastIndex);
			}
			
			idRefs.add(rInnerClass.getIdStaticRef(fieldName, holder));

		} else {

			idRefs.add(rInnerClass.getIdStaticRef(idValue, holder));

		}
		return idRefs;
	}

}
