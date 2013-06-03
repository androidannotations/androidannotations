package org.androidannotations.handler;

import org.androidannotations.handler.rest.GetHandler;
import org.androidannotations.handler.rest.RestHandler;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidRes;
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
		add(new SharedPrefHandler(processingEnvironment));
		add(new PrefHandler(processingEnvironment));
		add(new RoboGuiceHandler(processingEnvironment));
		add(new ViewByIdHandler(processingEnvironment));
		add(new FragmentByIdHandler(processingEnvironment));
		add(new FragmentByTagHandler(processingEnvironment));
		add(new FromHtmlHandler(processingEnvironment));
		add(new ClickHandler(processingEnvironment));
		add(new LongClickHandler(processingEnvironment));
		add(new TouchHandler(processingEnvironment));
		add(new FocusChangeHandler(processingEnvironment));
		add(new CheckedChangeHandler(processingEnvironment));
		add(new ItemClickHandler(processingEnvironment));
		add(new ItemSelectHandler(processingEnvironment));
		add(new ItemLongClickHandler(processingEnvironment));
		for (AndroidRes androidRes : AndroidRes.values()) {
			add(new ResHandler(androidRes, processingEnvironment));
		}
		add(new TransactionalHandler(processingEnvironment));
		add(new ExtraHandler(processingEnvironment));
		add(new FragmentArgHandler(processingEnvironment));
		add(new SystemServiceHandler(processingEnvironment));
		add(new RestHandler(processingEnvironment));
		add(new GetHandler(processingEnvironment));
		add(new PostHandler(processingEnvironment));

		add(new AfterInjectHandler(processingEnvironment));
        add(new InstanceStateHandler(processingEnvironment));
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
