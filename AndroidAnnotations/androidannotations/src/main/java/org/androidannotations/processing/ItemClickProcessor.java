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
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.ItemClick;
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

public class ItemClickProcessor implements DecoratingElementProcessor {

	private IdAnnotationHelper helper;

	public ItemClickProcessor(ProcessingEnvironment processingEnv, IRClass rClass) {
		helper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemClick.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeanHolder holder) {
		Classes classes = holder.classes();

		String methodName = element.getSimpleName().toString();

		ExecutableElement executableElement = (ExecutableElement) element;
		List<? extends VariableElement> parameters = executableElement.getParameters();

		boolean hasItemParameter = parameters.size() == 1;

		List<JFieldRef> idsRefs = helper.extractAnnotationFieldRefs(holder, element, Res.ID, true);

		JDefinedClass onItemClickListenerAnonymousClass = codeModel.anonymousClass(classes.ON_ITEM_CLICK_LISTENER);
		JMethod onItemClickMethod = onItemClickListenerAnonymousClass.method(JMod.PUBLIC, codeModel.VOID, "onItemClick");
		onItemClickMethod.annotate(Override.class);

		JClass narrowAdapterViewClass = classes.ADAPTER_VIEW.narrow(codeModel.wildcard());
		JVar onItemClickParentParam = onItemClickMethod.param(narrowAdapterViewClass, "parent");
		onItemClickMethod.param(classes.VIEW, "view");
		JVar onItemClickPositionParam = onItemClickMethod.param(codeModel.INT, "position");
		onItemClickMethod.param(codeModel.LONG, "id");

		JInvocation itemClickCall = onItemClickMethod.body().invoke(methodName);

		if (hasItemParameter) {
			VariableElement parameter = parameters.get(0);

			TypeMirror parameterType = parameter.asType();
			if (parameterType.getKind() == TypeKind.INT) {
				itemClickCall.arg(onItemClickPositionParam);
			} else {
				String parameterTypeQualifiedName = parameterType.toString();
				itemClickCall.arg(cast(holder.refClass(parameterTypeQualifiedName), invoke(onItemClickParentParam, "getAdapter").invoke("getItem").arg(onItemClickPositionParam)));
			}
		}

		ViewChangedHolder onViewChanged = holder.onViewChanged();
		for (JFieldRef idRef : idsRefs) {
			JBlock block = onViewChanged.body().block();

			JVar view = block.decl(narrowAdapterViewClass, "view", cast(narrowAdapterViewClass, onViewChanged.findViewById(idRef)));
			block._if(view.ne(JExpr._null()))._then().invoke(view, "setOnItemClickListener").arg(_new(onItemClickListenerAnonymousClass));
		}
	}

}
