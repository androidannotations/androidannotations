package org.androidannotations.handler;

import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

import javax.annotation.processing.ProcessingEnvironment;

public abstract class BaseAnnotationHandler<T extends GeneratedClassHolder> implements AnnotationHandler<T> {

	private final String target;
	protected ProcessingEnvironment processingEnv;
	protected IdValidatorHelper validatorHelper;
	protected IRClass rClass;
	protected AndroidSystemServices androidSystemServices;
	protected AndroidManifest androidManifest;
	protected AnnotationElements validatedModel;

	public BaseAnnotationHandler(Class<?> targetClass, ProcessingEnvironment processingEnvironment) {
		this(targetClass.getName(), processingEnvironment);
	}

	public BaseAnnotationHandler(String target, ProcessingEnvironment processingEnvironment) {
		this.target = target;
		this.processingEnv = processingEnvironment;
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		this.rClass = rClass;
		this.androidSystemServices = androidSystemServices;
		this.androidManifest = androidManifest;
		initValidatorHelper();
	}

	private void initValidatorHelper() {
		IdAnnotationHelper annotationHelper = new IdAnnotationHelper(processingEnv, target, rClass);
		validatorHelper = new IdValidatorHelper(annotationHelper);
	}

	@Override
	public void setValidatedModel(AnnotationElements validatedModel) {
		this.validatedModel = validatedModel;
	}

	@Override
	public String getTarget() {
		return target;
	}
}
