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
package org.androidannotations;

import org.androidannotations.handler.AfterExtrasHandler;
import org.androidannotations.handler.AfterInjectHandler;
import org.androidannotations.handler.AfterPreferencesHandler;
import org.androidannotations.handler.AfterTextChangeHandler;
import org.androidannotations.handler.AfterViewsHandler;
import org.androidannotations.handler.AnimationResHandler;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.handler.AppHandler;
import org.androidannotations.handler.BackgroundHandler;
import org.androidannotations.handler.BeanHandler;
import org.androidannotations.handler.BeforeTextChangeHandler;
import org.androidannotations.handler.CheckedChangeHandler;
import org.androidannotations.handler.ClickHandler;
import org.androidannotations.handler.CustomTitleHandler;
import org.androidannotations.handler.DefaultResHandler;
import org.androidannotations.handler.DrawableResHandler;
import org.androidannotations.handler.EActivityHandler;
import org.androidannotations.handler.EApplicationHandler;
import org.androidannotations.handler.EBeanHandler;
import org.androidannotations.handler.EFragmentHandler;
import org.androidannotations.handler.EIntentServiceHandler;
import org.androidannotations.handler.EProviderHandler;
import org.androidannotations.handler.EReceiverHandler;
import org.androidannotations.handler.EServiceHandler;
import org.androidannotations.handler.EViewGroupHandler;
import org.androidannotations.handler.EViewHandler;
import org.androidannotations.handler.EditorActionHandler;
import org.androidannotations.handler.ExtraHandler;
import org.androidannotations.handler.FocusChangeHandler;
import org.androidannotations.handler.FragmentArgHandler;
import org.androidannotations.handler.FragmentByIdHandler;
import org.androidannotations.handler.FragmentByTagHandler;
import org.androidannotations.handler.FromHtmlHandler;
import org.androidannotations.handler.FullscreenHandler;
import org.androidannotations.handler.HierarchyViewerSupportHandler;
import org.androidannotations.handler.HtmlResHandler;
import org.androidannotations.handler.HttpsClientHandler;
import org.androidannotations.handler.IgnoredWhenDetachedHandler;
import org.androidannotations.handler.InjectMenuHandler;
import org.androidannotations.handler.InstanceStateHandler;
import org.androidannotations.handler.ItemClickHandler;
import org.androidannotations.handler.ItemLongClickHandler;
import org.androidannotations.handler.ItemSelectHandler;
import org.androidannotations.handler.LongClickHandler;
import org.androidannotations.handler.NonConfigurationInstanceHandler;
import org.androidannotations.handler.OnActivityResultHandler;
import org.androidannotations.handler.OptionsItemHandler;
import org.androidannotations.handler.OptionsMenuHandler;
import org.androidannotations.handler.OptionsMenuItemHandler;
import org.androidannotations.handler.PrefHandler;
import org.androidannotations.handler.PreferenceByKeyHandler;
import org.androidannotations.handler.PreferenceChangeHandler;
import org.androidannotations.handler.PreferenceClickHandler;
import org.androidannotations.handler.PreferenceHeadersHandler;
import org.androidannotations.handler.PreferenceScreenHandler;
import org.androidannotations.handler.ReceiverActionHandler;
import org.androidannotations.handler.ReceiverHandler;
import org.androidannotations.handler.RoboGuiceHandler;
import org.androidannotations.handler.RootContextHandler;
import org.androidannotations.handler.SeekBarProgressChangeHandler;
import org.androidannotations.handler.SeekBarTouchStartHandler;
import org.androidannotations.handler.SeekBarTouchStopHandler;
import org.androidannotations.handler.ServiceActionHandler;
import org.androidannotations.handler.SharedPrefHandler;
import org.androidannotations.handler.SupposeBackgroundHandler;
import org.androidannotations.handler.SupposeUiThreadHandler;
import org.androidannotations.handler.SystemServiceHandler;
import org.androidannotations.handler.TextChangeHandler;
import org.androidannotations.handler.TouchHandler;
import org.androidannotations.handler.TraceHandler;
import org.androidannotations.handler.TransactionalHandler;
import org.androidannotations.handler.UiThreadHandler;
import org.androidannotations.handler.ViewByIdHandler;
import org.androidannotations.handler.ViewsByIdHandler;
import org.androidannotations.handler.WakeLockHandler;
import org.androidannotations.handler.WindowFeatureHandler;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;

public class CorePlugin extends AndroidAnnotationsPlugin {

	private static final String NAME = "AndroidAnnotations";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addHandlers(AnnotationHandlers annotationHandlers, AndroidAnnotationsEnvironment androidAnnotationEnv) {
		annotationHandlers.add(new EApplicationHandler(androidAnnotationEnv));
		annotationHandlers.add(new EActivityHandler(androidAnnotationEnv));
		annotationHandlers.add(new EProviderHandler(androidAnnotationEnv));
		annotationHandlers.add(new EReceiverHandler(androidAnnotationEnv));
		annotationHandlers.add(new EServiceHandler(androidAnnotationEnv));
		annotationHandlers.add(new EIntentServiceHandler(androidAnnotationEnv));
		annotationHandlers.add(new EFragmentHandler(androidAnnotationEnv));
		annotationHandlers.add(new EBeanHandler(androidAnnotationEnv));
		annotationHandlers.add(new EViewGroupHandler(androidAnnotationEnv));
		annotationHandlers.add(new EViewHandler(androidAnnotationEnv));
		annotationHandlers.add(new SharedPrefHandler(androidAnnotationEnv));
		annotationHandlers.add(new PrefHandler(androidAnnotationEnv));
		annotationHandlers.add(new RoboGuiceHandler(androidAnnotationEnv));
		annotationHandlers.add(new ViewByIdHandler(androidAnnotationEnv));
		annotationHandlers.add(new ViewsByIdHandler(androidAnnotationEnv));
		annotationHandlers.add(new FragmentByIdHandler(androidAnnotationEnv));
		annotationHandlers.add(new FragmentByTagHandler(androidAnnotationEnv));
		annotationHandlers.add(new FromHtmlHandler(androidAnnotationEnv));
		annotationHandlers.add(new ClickHandler(androidAnnotationEnv));
		annotationHandlers.add(new LongClickHandler(androidAnnotationEnv));
		annotationHandlers.add(new TouchHandler(androidAnnotationEnv));
		annotationHandlers.add(new FocusChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new CheckedChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new ItemClickHandler(androidAnnotationEnv));
		annotationHandlers.add(new ItemSelectHandler(androidAnnotationEnv));
		annotationHandlers.add(new ItemLongClickHandler(androidAnnotationEnv));
		annotationHandlers.add(new EditorActionHandler(androidAnnotationEnv));
		for (AndroidRes androidRes : AndroidRes.values()) {
			if (androidRes == AndroidRes.ANIMATION) {
				annotationHandlers.add(new AnimationResHandler(androidAnnotationEnv));
			} else if (androidRes == AndroidRes.DRAWABLE) {
				annotationHandlers.add(new DrawableResHandler(androidAnnotationEnv));
			} else if (androidRes == AndroidRes.HTML) {
				annotationHandlers.add(new HtmlResHandler(androidAnnotationEnv));
			} else {
				annotationHandlers.add(new DefaultResHandler(androidRes, androidAnnotationEnv));
			}
		}
		annotationHandlers.add(new TransactionalHandler(androidAnnotationEnv));
		annotationHandlers.add(new FragmentArgHandler(androidAnnotationEnv));
		annotationHandlers.add(new SystemServiceHandler(androidAnnotationEnv));

		annotationHandlers.add(new AppHandler(androidAnnotationEnv));
		annotationHandlers.add(new BeanHandler(androidAnnotationEnv));
		annotationHandlers.add(new InjectMenuHandler(androidAnnotationEnv));
		annotationHandlers.add(new OptionsMenuHandler(androidAnnotationEnv));
		annotationHandlers.add(new OptionsMenuItemHandler(androidAnnotationEnv));
		annotationHandlers.add(new OptionsItemHandler(androidAnnotationEnv));
		annotationHandlers.add(new CustomTitleHandler(androidAnnotationEnv));
		annotationHandlers.add(new FullscreenHandler(androidAnnotationEnv));
		annotationHandlers.add(new RootContextHandler(androidAnnotationEnv));
		annotationHandlers.add(new NonConfigurationInstanceHandler(androidAnnotationEnv));
		annotationHandlers.add(new ExtraHandler(androidAnnotationEnv));
		annotationHandlers.add(new BeforeTextChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new TextChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterTextChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new SeekBarProgressChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new SeekBarTouchStartHandler(androidAnnotationEnv));
		annotationHandlers.add(new SeekBarTouchStopHandler(androidAnnotationEnv));
		annotationHandlers.add(new ServiceActionHandler(androidAnnotationEnv));
		annotationHandlers.add(new InstanceStateHandler(androidAnnotationEnv));
		annotationHandlers.add(new HttpsClientHandler(androidAnnotationEnv));
		annotationHandlers.add(new HierarchyViewerSupportHandler(androidAnnotationEnv));
		annotationHandlers.add(new WindowFeatureHandler(androidAnnotationEnv));
		annotationHandlers.add(new ReceiverHandler(androidAnnotationEnv));
		annotationHandlers.add(new ReceiverActionHandler(androidAnnotationEnv));
		annotationHandlers.add(new OnActivityResultHandler(androidAnnotationEnv));

		annotationHandlers.add(new IgnoredWhenDetachedHandler(androidAnnotationEnv));
		/* After injection methods must be after injections */
		annotationHandlers.add(new AfterInjectHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterExtrasHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterViewsHandler(androidAnnotationEnv));

		/* preference screen handler must be after injections */
		annotationHandlers.add(new PreferenceScreenHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceHeadersHandler(androidAnnotationEnv));
		/* Preference injections must be after preference screen handler */
		annotationHandlers.add(new PreferenceByKeyHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceClickHandler(androidAnnotationEnv));
		/* After preference injection methods must be after preference injections */
		annotationHandlers.add(new AfterPreferencesHandler(androidAnnotationEnv));

		if (androidAnnotationEnv.getOptions().shouldLogTrace()) {
			annotationHandlers.add(new TraceHandler(androidAnnotationEnv));
		}

		/*
		 * WakeLockHandler must be after TraceHandler but before UiThreadHandler
		 * and BackgroundHandler
		 */
		annotationHandlers.add(new WakeLockHandler(androidAnnotationEnv));

		/*
		 * UIThreadHandler and BackgroundHandler must be after TraceHandler and
		 * IgnoredWhenDetached
		 */
		annotationHandlers.add(new UiThreadHandler(androidAnnotationEnv));
		annotationHandlers.add(new BackgroundHandler(androidAnnotationEnv));

		/*
		 * SupposeUiThreadHandler and SupposeBackgroundHandler must be after all
		 * handlers that modifies generated method body
		 */
		if (androidAnnotationEnv.getOptions().shouldEnsureThreadControl()) {
			annotationHandlers.add(new SupposeUiThreadHandler(androidAnnotationEnv));
			annotationHandlers.add(new SupposeBackgroundHandler(androidAnnotationEnv));
		}
	}
}
