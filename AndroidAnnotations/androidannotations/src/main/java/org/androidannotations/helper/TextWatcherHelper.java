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
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JMod.FINAL;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.processing.EBeanHolder;
import org.androidannotations.processing.TextWatcherHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class TextWatcherHelper extends IdAnnotationHelper {

	private final APTCodeModelHelper codeModelHelper;

	public TextWatcherHelper(//
			ProcessingEnvironment processingEnv, //
			Class<? extends Annotation> target, //
			IRClass rClass, //
			APTCodeModelHelper codeModelHelper) {

		super(processingEnv, target, rClass);

		this.codeModelHelper = codeModelHelper;

	}

	public TextWatcherHolder getOrCreateListener(JCodeModel codeModel, EBeanHolder holder, JFieldRef idRef, TypeMirror viewParameterType) {

		String idRefString = codeModelHelper.getIdStringFromIdFieldRef(idRef);
		TextWatcherHolder textWatcherHolder = holder.textWatchers.get(idRefString);

		if (textWatcherHolder == null) {
			JClass charSequenceClass = holder.classes().CHAR_SEQUENCE;

			JDefinedClass onTextChangeListenerClass = codeModel.anonymousClass(holder.classes().TEXT_WATCHER);

			JMethod afterTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "afterTextChanged");
			afterTextChangedMethod.param(holder.classes().EDITABLE, "s");
			afterTextChangedMethod.annotate(Override.class);

			JMethod onTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "onTextChanged");
			onTextChangedMethod.param(charSequenceClass, "s");
			onTextChangedMethod.param(codeModel.INT, "start");
			onTextChangedMethod.param(codeModel.INT, "before");
			onTextChangedMethod.param(codeModel.INT, "count");
			onTextChangedMethod.annotate(Override.class);

			JMethod beforeTextChangedMethod = onTextChangeListenerClass.method(JMod.PUBLIC, codeModel.VOID, "beforeTextChanged");
			beforeTextChangedMethod.param(charSequenceClass, "s");
			beforeTextChangedMethod.param(codeModel.INT, "start");
			beforeTextChangedMethod.param(codeModel.INT, "count");
			beforeTextChangedMethod.param(codeModel.INT, "after");
			beforeTextChangedMethod.annotate(Override.class);

			JBlock block = holder.onViewChanged().body().block();

			JClass viewClass;
			if (viewParameterType != null) {
				String viewParameterTypeString = viewParameterType.toString();
				viewClass = holder.refClass(viewParameterTypeString);
			} else {
				viewClass = holder.classes().TEXT_VIEW;
			}
			JExpression findViewById = cast(viewClass, holder.onViewChanged().findViewById(idRef));

			JVar viewVariable = block.decl(FINAL, viewClass, "view", findViewById);
			block._if(viewVariable.ne(JExpr._null()))._then().invoke(viewVariable, "addTextChangedListener").arg(_new(onTextChangeListenerClass));

			textWatcherHolder = new TextWatcherHolder(//
					afterTextChangedMethod, //
					beforeTextChangedMethod, //
					onTextChangedMethod, //
					viewVariable);

			holder.textWatchers.put(idRefString, textWatcherHolder);
		}

		return textWatcherHolder;
	}

}
