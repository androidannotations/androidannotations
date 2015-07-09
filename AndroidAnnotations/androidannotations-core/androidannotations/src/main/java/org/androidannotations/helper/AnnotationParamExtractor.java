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
package org.androidannotations.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import org.androidannotations.holder.GeneratedClassHolder;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;

public class AnnotationParamExtractor extends SimpleAnnotationValueVisitor6<Void, String> {

	private JAnnotationUse use;
	private GeneratedClassHolder holder;
	private APTCodeModelHelper helper;

	public AnnotationParamExtractor(JAnnotationUse use, GeneratedClassHolder holder, APTCodeModelHelper helper) {
		this.use = use;
		this.holder = holder;
		this.helper = helper;
	}

	@Override
	public Void visitArray(List<? extends AnnotationValue> vals, String p) {
		JAnnotationArrayMember paramArray = use.paramArray(p);

		for (AnnotationValue annotationValue : vals) {
			annotationValue.accept(new AnnotationArrayParamExtractor(holder, helper), paramArray);
		}

		return null;
	}

	@Override
	public Void visitBoolean(boolean b, String p) {
		use.param(p, b);
		return null;
	}

	@Override
	public Void visitByte(byte b, String p) {
		use.param(p, b);
		return null;
	}

	@Override
	public Void visitChar(char c, String p) {
		use.param(p, c);
		return null;
	}

	@Override
	public Void visitDouble(double d, String p) {
		use.param(p, d);
		return null;
	}

	@Override
	public Void visitFloat(float f, String p) {
		use.param(p, f);
		return null;
	}

	@Override
	public Void visitInt(int i, String p) {
		use.param(p, i);
		return null;
	}

	@Override
	public Void visitLong(long i, String p) {
		use.param(p, i);
		return null;
	}

	@Override
	public Void visitShort(short s, String p) {
		use.param(p, s);
		return null;
	}

	@Override
	public Void visitString(String s, String p) {
		use.param(p, s);
		return null;
	}

	@Override
	public Void visitEnumConstant(VariableElement c, String p) {
		JClass annotationClass = helper.typeMirrorToJClass(c.asType(), holder);
		JExpression expression = JExpr.direct(annotationClass.fullName() + "." + c.getSimpleName());
		use.param(p, expression);
		return null;
	}

	@Override
	public Void visitType(TypeMirror t, String p) {
		JClass annotationClass = helper.typeMirrorToJClass(t, holder);
		JExpression dotclass = JExpr.dotclass(annotationClass);
		use.param(p, dotclass);
		return null;
	}

	@Override
	public Void visitAnnotation(AnnotationMirror a, String p) {
		try {
			JClass annotationJClass = helper.typeMirrorToJClass(a.getAnnotationType(), holder);
			Constructor<JAnnotationUse> constructor = JAnnotationUse.class.getDeclaredConstructor(JClass.class);
			constructor.setAccessible(true);
			JAnnotationUse paramAnnotation = constructor.newInstance(annotationJClass);
			Map<? extends ExecutableElement, ? extends AnnotationValue> parameters = a.getElementValues();
			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> param : parameters.entrySet()) {
				param.getValue().accept(new AnnotationParamExtractor(paramAnnotation, holder, helper), param.getKey().getSimpleName().toString());
			}

			Method addValueMethod = use.getClass().getDeclaredMethod("addValue", String.class, JAnnotationValue.class);
			addValueMethod.setAccessible(true);
			addValueMethod.invoke(use, p, paramAnnotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
};
