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
package org.androidannotations.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.androidannotations.helper.OptionsHelper;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

public class AnnotationHandlers {

	private List<AnnotationHandler<? extends GeneratedClassHolder>> annotationHandlers = new ArrayList<>();
	private List<GeneratingAnnotationHandler<? extends GeneratedClassHolder>> generatingAnnotationHandlers = new ArrayList<>();
	private List<AnnotationHandler<? extends GeneratedClassHolder>> decoratingAnnotationHandlers = new ArrayList<>();
	private Set<String> supportedAnnotationNames;
	private OptionsHelper optionsHelper;

	public AnnotationHandlers(ProcessingEnvironment processingEnvironment) {
		optionsHelper = new OptionsHelper(processingEnvironment);

		add(new EApplicationHandler(processingEnvironment));
		add(new EActivityHandler(processingEnvironment));
		add(new EProviderHandler(processingEnvironment));
		add(new EReceiverHandler(processingEnvironment));
		add(new EServiceHandler(processingEnvironment));
		add(new EIntentServiceHandler(processingEnvironment));
		add(new EFragmentHandler(processingEnvironment));
		add(new EBeanHandler(processingEnvironment));
		add(new EViewGroupHandler(processingEnvironment));
		add(new EViewHandler(processingEnvironment));
		add(new SharedPrefHandler(processingEnvironment));
		add(new PrefHandler(processingEnvironment));
		add(new RoboGuiceHandler(processingEnvironment));
		add(new ViewByIdHandler(processingEnvironment));
		add(new ViewsByIdHandler(processingEnvironment));
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
		add(new EditorActionHandler(processingEnvironment));
		for (AndroidRes androidRes : AndroidRes.values()) {
			if (androidRes == AndroidRes.ANIMATION) {
				add(new AnimationResHandler(processingEnvironment));
			} else if (androidRes == AndroidRes.DRAWABLE) {
				add(new DrawableResHandler(processingEnvironment));
			} else if (androidRes == AndroidRes.HTML) {
				add(new HtmlResHandler(processingEnvironment));
			} else {
				add(new DefaultResHandler(androidRes, processingEnvironment));
			}
		}
		add(new TransactionalHandler(processingEnvironment));
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
		add(new BeanHandler(processingEnvironment));
		add(new InjectMenuHandler(processingEnvironment));
		add(new OptionsMenuHandler(processingEnvironment));
		add(new OptionsMenuItemHandler(processingEnvironment));
		add(new OptionsItemHandler(processingEnvironment));
		add(new CustomTitleHandler(processingEnvironment));
		add(new FullscreenHandler(processingEnvironment));
		add(new RestServiceHandler(processingEnvironment));
		add(new OrmLiteDaoHandler(processingEnvironment));
		add(new RootContextHandler(processingEnvironment));
		add(new NonConfigurationInstanceHandler(processingEnvironment));
		add(new ExtraHandler(processingEnvironment));
		add(new BeforeTextChangeHandler(processingEnvironment));
		add(new TextChangeHandler(processingEnvironment));
		add(new AfterTextChangeHandler(processingEnvironment));
		add(new SeekBarProgressChangeHandler(processingEnvironment));
		add(new SeekBarTouchStartHandler(processingEnvironment));
		add(new SeekBarTouchStopHandler(processingEnvironment));
		add(new ServiceActionHandler(processingEnvironment));
		add(new ProduceHandler(processingEnvironment));
		add(new SubscribeHandler(processingEnvironment));
		add(new InstanceStateHandler(processingEnvironment));
		add(new HttpsClientHandler(processingEnvironment));
		add(new HierarchyViewerSupportHandler(processingEnvironment));
		add(new WindowFeatureHandler(processingEnvironment));
		new ReceiverHandler(processingEnvironment).register(this);
		new ReceiverActionHandler(processingEnvironment).register(this);
		new OnActivityResultHandler(processingEnvironment).register(this);

		add(new IgnoredWhenDetachedHandler(processingEnvironment));
		/* After injection methods must be after injections */
		add(new AfterInjectHandler(processingEnvironment));
		add(new AfterExtrasHandler(processingEnvironment));
		add(new AfterViewsHandler(processingEnvironment));

		/* preference screen handler must be after injections */
		add(new PreferenceScreenHandler(processingEnvironment));
		add(new PreferenceHeadersHandler(processingEnvironment));
		/* Preference injections must be after preference screen handler */
		add(new PreferenceByKeyHandler(processingEnvironment));
		add(new PreferenceChangeHandler(processingEnvironment));
		add(new PreferenceClickHandler(processingEnvironment));
		/* After preference injection methods must be after preference injections */
		add(new AfterPreferencesHandler(processingEnvironment));

		if (optionsHelper.shouldLogTrace()) {
			add(new TraceHandler(processingEnvironment));
		}

		/*
		 * WakeLockHandler must be after TraceHandler but before UiThreadHandler
		 * and BackgroundHandler
		 */
		add(new WakeLockHandler(processingEnvironment));

		/*
		 * UIThreadHandler and BackgroundHandler must be after TraceHandler and
		 * IgnoredWhenDetached
		 */
		add(new UiThreadHandler(processingEnvironment));
		add(new BackgroundHandler(processingEnvironment));

		/*
		 * SupposeUiThreadHandler and SupposeBackgroundHandler must be after all
		 * handlers that modifies generated method body
		 */
		if (optionsHelper.shouldEnsureThreadControl()) {
			add(new SupposeUiThreadHandler(processingEnvironment));
			add(new SupposeBackgroundHandler(processingEnvironment));
		}
	}

	public void add(AnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
		annotationHandlers.add(annotationHandler);
		decoratingAnnotationHandlers.add(annotationHandler);
	}

	public void add(GeneratingAnnotationHandler<? extends GeneratedClassHolder> annotationHandler) {
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

	public void setProcessHolder(ProcessHolder processHolder) {
		for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
			annotationHandler.setProcessHolder(processHolder);
		}
	}

	public Set<String> getSupportedAnnotationTypes() {
		if (supportedAnnotationNames == null) {
			Set<String> annotationNames = new HashSet<>();
			for (AnnotationHandler<?> annotationHandler : annotationHandlers) {
				annotationNames.add(annotationHandler.getTarget());
			}
			supportedAnnotationNames = Collections.unmodifiableSet(annotationNames);
		}
		return supportedAnnotationNames;
	}
}
