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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import org.androidannotations.helper.APTCodeModelHelper;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JAnnotationArrayMember;

public class AnnotationArrayParamExtractor extends SimpleAnnotationValueVisitor6<Void, JAnnotationArrayMember> {

	private APTCodeModelHelper helper;

	public AnnotationArrayParamExtractor(APTCodeModelHelper helper) {
		this.helper = helper;
	}

	@Override
	public Void visitBoolean(boolean b, JAnnotationArrayMember p) {
		p.param(b);
		return null;
	}

	@Override
	public Void visitByte(byte b, JAnnotationArrayMember p) {
		p.param(b);
		return null;
	}

	@Override
	public Void visitChar(char c, JAnnotationArrayMember p) {
		p.param(c);
		return null;
	}

	@Override
	public Void visitDouble(double d, JAnnotationArrayMember p) {
		p.param(d);
		return null;
	}

	@Override
	public Void visitFloat(float f, JAnnotationArrayMember p) {
		p.param(f);
		return null;
	}

	@Override
	public Void visitInt(int i, JAnnotationArrayMember p) {
		p.param(i);
		return null;
	}

	@Override
	public Void visitLong(long i, JAnnotationArrayMember p) {
		p.param(i);
		return null;
	}

	@Override
	public Void visitShort(short s, JAnnotationArrayMember p) {
		p.param(s);
		return null;
	}

	@Override
	public Void visitString(String s, JAnnotationArrayMember p) {
		p.param(s);
		return null;
	}

	@Override
	public Void visitType(TypeMirror t, JAnnotationArrayMember p) {
		AbstractJClass annotationClass = helper.typeMirrorToJClass(t);
		p.param(annotationClass);
		return null;
	}

	@Override
	public Void visitEnumConstant(VariableElement c, JAnnotationArrayMember p) {
		AbstractJClass annotationClass = helper.typeMirrorToJClass(c.asType());
		IJExpression expression = annotationClass.staticRef(c.getSimpleName().toString());
		p.param(expression);
		return null;
	}

	@Override
	public Void visitAnnotation(AnnotationMirror a, JAnnotationArrayMember p) {
		helper.copyAnnotation(p, a);
		return null;
	}
}
