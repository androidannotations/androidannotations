package org.androidannotations.holder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.androidannotations.process.ProcessHolder.Classes;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public abstract class GeneratedClassHolderDelegate<T extends GeneratedClassHolder> implements GeneratedClassHolder {

	protected T holder;

	public GeneratedClassHolderDelegate(T holder) {
		this.holder = holder;
	}

	@Override
	public final JDefinedClass getGeneratedClass() {
		return holder.getGeneratedClass();
	}

	@Override
	public final TypeElement getAnnotatedElement() {
		return holder.getAnnotatedElement();
	}

	@Override
	public final ProcessingEnvironment processingEnvironment() {
		return holder.processingEnvironment();
	}

	@Override
	public final Classes classes() {
		return holder.classes();
	}

	@Override
	public final JCodeModel codeModel() {
		return holder.codeModel();
	}

	@Override
	public final JClass refClass(String fullyQualifiedClassName) {
		return holder.refClass(fullyQualifiedClassName);
	}

	@Override
	public final JClass refClass(Class<?> clazz) {
		return holder.refClass(clazz);
	}

	@Override
	public final JDefinedClass definedClass(String fullyQualifiedClassName) {
		return holder.definedClass(fullyQualifiedClassName);
	}
}
