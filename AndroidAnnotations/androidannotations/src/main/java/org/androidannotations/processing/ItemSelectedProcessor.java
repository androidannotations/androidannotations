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

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.processing.EBeansHolder.Classes;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRClass.Res;

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
 */
public class ItemSelectedProcessor implements DecoratingElementProcessor {

	private IdAnnotationHelper helper;

	public ItemSelectedProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemSelect.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		JDefinedClass onItemSelectedListenerClass = codeModel.anonymousClass(classes.ON_ITEM_SELECTED_LISTENER);
		JMethod onItemSelectedMethod = onItemSelectedListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onItemSelected");
		onItemSelectedMethod.annotate(Override.class);

		JClass narrowAdapterViewClass = classes.ADAPTER_VIEW.narrow(codeModel.wildcard());
		JVar onItemClickParentParam = onItemSelectedMethod.param(narrowAdapterViewClass, "parent");
		onItemSelectedMethod.param(classes.VIEW, "view");
		JVar onItemClickPositionParam = onItemSelectedMethod.param(codeModel.INT, "position");
		onItemSelectedMethod.param(codeModel.LONG, "id");

		JInvocation itemSelectedCall = onItemSelectedMethod.body().invoke(methodName);

		itemSelectedCall.arg(JExpr.TRUE);

		boolean hasItemParameter = parameters.size() == 2;
		boolean secondParameterIsInt = false;
		String secondParameterQualifiedName = null;
		if (hasItemParameter) {
			VariableElement secondParameter = parameters.get(1);
			TypeMirror secondParameterType = secondParameter.asType();
			secondParameterQualifiedName = secondParameterType.toString();
			secondParameterIsInt = secondParameterType.getKind() == TypeKind.INT;
		}

		if (hasItemParameter) {

			if (secondParameterIsInt) {
				itemSelectedCall.arg(onItemClickPositionParam);
			} else {
				itemSelectedCall.arg(JExpr.cast(holder.refClass(secondParameterQualifiedName), invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
			}
		}

		JMethod onNothingSelectedMethod = onItemSelectedListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onNothingSelected");
		onNothingSelectedMethod.annotate(Override.class);

		onNothingSelectedMethod.param(narrowAdapterViewClass, "parent");

		JInvocation nothingSelectedCall = onNothingSelectedMethod.body().invoke(methodName);

		nothingSelectedCall.arg(JExpr.FALSE);
		if (hasItemParameter) {
			if (secondParameterIsInt) {
				nothingSelectedCall.arg(lit(-1));
			} else {
				nothingSelectedCall.arg(_null());
			}
		}

		ViewChangedHolder onViewChanged = holder.onViewChanged();
		for (JFieldRef idRef : idsRefs) {
			JBlock body = onViewChanged.body();
			body.add(invoke(cast(narrowAdapterViewClass, onViewChanged.findViewById(idRef)), "setOnItemSelectedListener").arg(_new(onItemSelectedListenerClass)));
		}
	}

}
