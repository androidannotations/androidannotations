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
package org.androidannotations.handler;

import static org.androidannotations.helper.ModelConstants.TRACE_OPTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

import org.androidannotations.handler.rest.DeleteHandler;
import org.androidannotations.handler.rest.GetHandler;
import org.androidannotations.handler.rest.HeadHandler;
import org.androidannotations.handler.rest.OptionsHandler;
import org.androidannotations.handler.rest.PostHandler;
import org.androidannotations.handler.rest.PutHandler;
import org.androidannotations.handler.rest.RestHandler;
import org.androidannotations.handler.rest.RestServiceHandler;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;

public class AnnotationHandlers {

	private List<AnnotationHandler<? extends GeneratedClassHolder>> annotationHandlers = new ArrayList<AnnotationHandler<? extends GeneratedClassHolder>>();
	private List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> generatingAnnotationHandlers = new ArrayList<GeneratingAnnotationHandler<? extends GeneratedClassHolder>>();
	private List<AnnotationHandler<? extends GeneratedClassHolder>> decoratingAnnotationHandlers = new ArrayList<AnnotationHandler<? extends GeneratedClassHolder>>();
	private Set<String> supportedAnnotationNames;

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
		add(new PutHandler(processingEnvironment));
		add(new DeleteHandler(processingEnvironment));
		add(new HeadHandler(processingEnvironment));
		add(new OptionsHandler(processingEnvironment));
		add(new AppHandler(processingEnvironment));
		add(new OptionsMenuHandler(processingEnvironment));
		add(new OptionsMenuItemHandler(processingEnvironment));
		add(new OptionsItemHandler(processingEnvironment));
		add(new NoTitleHandler(processingEnvironment));
		add(new CustomTitleHandler(processingEnvironment));
		add(new FullscreenHandler(processingEnvironment));
		add(new RestServiceHandler(processingEnvironment));
		add(new OrmLiteDaoHandler(processingEnvironment));
		add(new RootContextHanlder(processingEnvironment));
		add(new NonConfigurationInstanceHandler(processingEnvironment));
		add(new BeanHandler(processingEnvironment));
		add(new BeforeTextChangeHandler(processingEnvironment));
		add(new TextChangeHandler(processingEnvironment));
		add(new AfterTextChangeHandler(processingEnvironment));
		add(new SeekBarProgressChangeHandler(processingEnvironment));
		add(new SeekBarTouchStartHandler(processingEnvironment));
		add(new SeekBarTouchStopHandler(processingEnvironment));
		add(new SubscribeHandler(processingEnvironment));
		add(new ProduceHandler(processingEnvironment));
		add(new UiThreadHandler(processingEnvironment));
		add(new BackgroundHandler(processingEnvironment));
		add(new InstanceStateHandler(processingEnvironment));
		add(new HttpsClientHandler(processingEnvironment));
		add(new OnActivityResultHandler(processingEnvironment));
		add(new HierarchyViewerSupportHandler(processingEnvironment));
		add(new WindowFeatureHandler(processingEnvironment));

		/* After injection methods must be after injections */
		add(new AfterInjectHandler(processingEnvironment));
		add(new AfterViewsHandler(processingEnvironment));

		if (traceActivated(processingEnvironment)) {
			add(new TraceHandler(processingEnvironment));
		}
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
		for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
			annotationHandler.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		}
	}

	public void setValidatedModel(AnnotationElements validatedModel) {
		for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
			annotationHandler.setValidatedModel(validatedModel);
		}
	}

	private boolean traceActivated(ProcessingEnvironment processingEnvironment) {
		Map<String, String> options = processingEnvironment.getOptions();
		if (options.containsKey(TRACE_OPTION)) {
			String trace = options.get(TRACE_OPTION);
			return !"false".equals(trace);
		} else {
			return true;
		}
	}

	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationNames == null) {
			Set<String> annotationNames = new HashSet<String>();
			for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
				annotationNames.add(annotationHandler.getTarget());
			}
			supportedAnnotationNames = Collections.unmodifiableSet(annotationNames);
		}
		return supportedAnnotationNames;
	}
}
