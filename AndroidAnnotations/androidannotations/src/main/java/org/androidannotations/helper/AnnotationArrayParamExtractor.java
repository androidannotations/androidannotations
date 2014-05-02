package org.androidannotations.helper;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;

import org.androidannotations.holder.GeneratedClassHolder;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;

public class AnnotationArrayParamExtractor extends SimpleAnnotationValueVisitor6<Void, JAnnotationArrayMember> {

	private GeneratedClassHolder holder;
	private APTCodeModelHelper helper;

	public AnnotationArrayParamExtractor(GeneratedClassHolder holder, APTCodeModelHelper helper) {
		this.holder = holder;
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
		JClass annotationClass = helper.typeMirrorToJClass(t, holder);
		JExpression dotclass = JExpr.dotclass(annotationClass);
		p.param(dotclass);
		return null;
	}

	@Override
	public Void visitEnumConstant(VariableElement c, JAnnotationArrayMember p) {
		JClass annotationClass = helper.typeMirrorToJClass(c.asType(), holder);
		JExpression expression = JExpr.direct(annotationClass.fullName() + "." + c.getSimpleName());
		p.param(expression);
		return null;
	}

	@Override
	public Void visitAnnotation(AnnotationMirror a, JAnnotationArrayMember p) {
		// TODO
		return null;
	}
}
