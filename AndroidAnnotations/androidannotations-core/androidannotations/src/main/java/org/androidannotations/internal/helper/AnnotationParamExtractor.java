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
package org.androidannotations.internal.helper;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import org.androidannotations.helper.APTCodeModelHelper;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JAnnotationArrayMember;
import com.helger.jcodemodel.JAnnotationUse;

public class AnnotationParamExtractor extends SimpleAnnotationValueVisitor6<Void, String> {

	private JAnnotationUse use;
	private APTCodeModelHelper helper;

	public AnnotationParamExtractor(JAnnotationUse use, APTCodeModelHelper helper) {
		this.use = use;
		this.helper = helper;
	}

	@Override
	public Void visitArray(List<? extends AnnotationValue> vals, String p) {
		JAnnotationArrayMember paramArray = use.paramArray(p);

		for (AnnotationValue annotationValue : vals) {
			annotationValue.accept(new AnnotationArrayParamExtractor(helper), paramArray);
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
		AbstractJClass annotationClass = helper.typeMirrorToJClass(c.asType());
		IJExpression expression = annotationClass.staticRef(c.getSimpleName().toString());
		use.param(p, expression);
		return null;
	}

	@Override
	public Void visitType(TypeMirror t, String p) {
		AbstractJClass annotationClass = helper.typeMirrorToJClass(t);
		use.param(p, annotationClass);
		return null;
	}

	@Override
	public Void visitAnnotation(AnnotationMirror a, String p) {
		AbstractJClass annotationJClass = helper.typeMirrorToJClass(a.getAnnotationType());
		use.annotationParam(p, annotationJClass);
		return null;
	}
};
