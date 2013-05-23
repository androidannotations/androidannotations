package org.androidannotations.handler;

import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.*;

public class AnnotationHandlers {

	private List<AnnotationHandler<? extends GeneratedClassHolder>> annotationHandlers = new ArrayList<AnnotationHandler<? extends GeneratedClassHolder>>();
	private List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> generatingAnnotationHandlers = new ArrayList<GeneratingAnnotationHandler<? extends GeneratedClassHolder>>();
	private List<AnnotationHandler<? extends GeneratedClassHolder>> decoratingAnnotationHandlers = new ArrayList<AnnotationHandler<? extends GeneratedClassHolder>>();
	private Set<String> supportedAnnotationNames;

    @SuppressWarnings("unchecked")
	public AnnotationHandlers(ProcessingEnvironment processingEnvironment) {
		add(new EApplicationHandler(processingEnvironment));
		add(new EActivityHandler(processingEnvironment));
		add(new EProviderHandler(processingEnvironment));
		add(new EReceiverHandler(processingEnvironment));
		add(new EServiceHandler(processingEnvironment));
		add(new EFragmentHandler(processingEnvironment));
		add(new EBeanHandler(processingEnvironment));
		add(new EViewGroupHandler(processingEnvironment));
		add(new EViewHandler(processingEnvironment));
		add(new AfterInjectHandler(processingEnvironment));
	}

	private void add(AnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		decoratingAnnotationHandlers.add(annotationHandler);
	}

	private void add(GeneratingAnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		generatingAnnotationHandlers.add(annotationHandler);
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> get() {
		return annotationHandlers;
	}

	public List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> getGenerating() {
		return generatingAnnotationHandlers;
	}

	public List<AnnotationHandler<? extends GeneratedClassHolder>> getDecorating() {
		return decoratingAnnotationHandlers;
	}

	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		for (AnnotationHandler annotationHandler : annotationHandlers) {
			annotationHandler.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		}
	}

	public void setValidatedModel(AnnotationElements validatedModel) {
		for (AnnotationHandler annotationHandler : annotationHandlers) {
			annotationHandler.setValidatedModel(validatedModel);
		}
	}

	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationNames == null) {
			Set<String> annotationNames = new HashSet<String>();
			for (AnnotationHandler annotationHandler : annotationHandlers) {
				annotationNames.add(annotationHandler.getTarget());
			}
			supportedAnnotationNames = Collections.unmodifiableSet(annotationNames);
		}
		return supportedAnnotationNames;
	}
}
