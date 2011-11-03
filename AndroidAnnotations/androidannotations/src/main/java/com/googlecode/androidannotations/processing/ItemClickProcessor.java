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
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.rclass.IRClass;
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

/**
 * @author Benjamin Fellous
 * @author Pierre-Yves Ricau
 * @author Mathieu Boniface
 */
public class ItemClickProcessor extends MultipleResIdsBasedProcessor implements ElementProcessor {

	public ItemClickProcessor(IRClass rClass) {
		super (rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemClick.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) {
		EBeanHolder holder = activitiesHolder.getEnclosingActivityHolder(element);

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean hasItemParameter = parameters.size() == 1;
		
		ItemClick annotation = element.getAnnotation(ItemClick.class);
		List<JFieldRef> idsRefs = extractQualifiedIds(element, annotation.value(), "ItemClicked", holder);

		JDefinedClass onItemClickListenerClass = codeModel.anonymousClass(holder.refClass("android.widget.AdapterView.OnItemClickListener"));
		JMethod onItemClickMethod = onItemClickListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onItemClick");
		JClass adapterViewClass = holder.refClass("android.widget.AdapterView");
		JClass viewClass = holder.refClass("android.view.View");
		
		JClass narrowAdapterViewClass = adapterViewClass.narrow(codeModel.wildcard());
		JVar onItemClickParentParam = onItemClickMethod.param(narrowAdapterViewClass, "parent");
		onItemClickMethod.param(viewClass, "view");
		JVar onItemClickPositionParam = onItemClickMethod.param(codeModel.INT, "position");
		onItemClickMethod.param(codeModel.LONG, "id");
		
		JInvocation itemClickCall = onItemClickMethod.body().invoke(methodName);

		if (hasItemParameter) {
			VariableElement parameter = parameters.get(0);
			String parameterQualifiedName = parameter.asType().toString();
			itemClickCall.arg(JExpr.cast(holder.refClass(parameterQualifiedName), JExpr.invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
		}
		
		for (JFieldRef idRef : idsRefs) {
			JBlock block = holder.afterSetContentView.body().block();
			JInvocation findViewById = JExpr.invoke("findViewById");
			
			JVar view = block.decl(narrowAdapterViewClass, "view", JExpr.cast(narrowAdapterViewClass, findViewById.arg(idRef)));
			block._if(view.ne(JExpr._null()))._then().invoke(view, "setOnItemClickListener").arg(JExpr._new(onItemClickListenerClass));
		}
	}

}
