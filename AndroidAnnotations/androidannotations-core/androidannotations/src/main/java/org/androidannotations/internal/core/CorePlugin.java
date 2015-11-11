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
package org.androidannotations.internal.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.Option;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.internal.core.handler.AfterExtrasHandler;
import org.androidannotations.internal.core.handler.AfterInjectHandler;
import org.androidannotations.internal.core.handler.AfterPreferencesHandler;
import org.androidannotations.internal.core.handler.AfterTextChangeHandler;
import org.androidannotations.internal.core.handler.AfterViewsHandler;
import org.androidannotations.internal.core.handler.AnimationResHandler;
import org.androidannotations.internal.core.handler.AppHandler;
import org.androidannotations.internal.core.handler.BackgroundHandler;
import org.androidannotations.internal.core.handler.BeanHandler;
import org.androidannotations.internal.core.handler.BeforeTextChangeHandler;
import org.androidannotations.internal.core.handler.CheckedChangeHandler;
import org.androidannotations.internal.core.handler.ClickHandler;
import org.androidannotations.internal.core.handler.ColorResHandler;
import org.androidannotations.internal.core.handler.ColorStateListResHandler;
import org.androidannotations.internal.core.handler.CustomTitleHandler;
import org.androidannotations.internal.core.handler.DefaultResHandler;
import org.androidannotations.internal.core.handler.DrawableResHandler;
import org.androidannotations.internal.core.handler.EActivityHandler;
import org.androidannotations.internal.core.handler.EApplicationHandler;
import org.androidannotations.internal.core.handler.EBeanHandler;
import org.androidannotations.internal.core.handler.EFragmentHandler;
import org.androidannotations.internal.core.handler.EIntentServiceHandler;
import org.androidannotations.internal.core.handler.EProviderHandler;
import org.androidannotations.internal.core.handler.EReceiverHandler;
import org.androidannotations.internal.core.handler.EServiceHandler;
import org.androidannotations.internal.core.handler.EViewGroupHandler;
import org.androidannotations.internal.core.handler.EViewHandler;
import org.androidannotations.internal.core.handler.EditorActionHandler;
import org.androidannotations.internal.core.handler.ExtraHandler;
import org.androidannotations.internal.core.handler.FocusChangeHandler;
import org.androidannotations.internal.core.handler.FragmentArgHandler;
import org.androidannotations.internal.core.handler.FragmentByIdHandler;
import org.androidannotations.internal.core.handler.FragmentByTagHandler;
import org.androidannotations.internal.core.handler.FromHtmlHandler;
import org.androidannotations.internal.core.handler.FullscreenHandler;
import org.androidannotations.internal.core.handler.HierarchyViewerSupportHandler;
import org.androidannotations.internal.core.handler.HtmlResHandler;
import org.androidannotations.internal.core.handler.HttpsClientHandler;
import org.androidannotations.internal.core.handler.IgnoredWhenDetachedHandler;
import org.androidannotations.internal.core.handler.InjectMenuHandler;
import org.androidannotations.internal.core.handler.InstanceStateHandler;
import org.androidannotations.internal.core.handler.ItemClickHandler;
import org.androidannotations.internal.core.handler.ItemLongClickHandler;
import org.androidannotations.internal.core.handler.ItemSelectHandler;
import org.androidannotations.internal.core.handler.KeyDownHandler;
import org.androidannotations.internal.core.handler.KeyLongPressHandler;
import org.androidannotations.internal.core.handler.KeyMultipleHandler;
import org.androidannotations.internal.core.handler.KeyUpHandler;
import org.androidannotations.internal.core.handler.LongClickHandler;
import org.androidannotations.internal.core.handler.NonConfigurationInstanceHandler;
import org.androidannotations.internal.core.handler.OnActivityResultHandler;
import org.androidannotations.internal.core.handler.OptionsItemHandler;
import org.androidannotations.internal.core.handler.OptionsMenuHandler;
import org.androidannotations.internal.core.handler.OptionsMenuItemHandler;
import org.androidannotations.internal.core.handler.PrefHandler;
import org.androidannotations.internal.core.handler.PreferenceByKeyHandler;
import org.androidannotations.internal.core.handler.PreferenceChangeHandler;
import org.androidannotations.internal.core.handler.PreferenceClickHandler;
import org.androidannotations.internal.core.handler.PreferenceHeadersHandler;
import org.androidannotations.internal.core.handler.PreferenceScreenHandler;
import org.androidannotations.internal.core.handler.ReceiverActionHandler;
import org.androidannotations.internal.core.handler.ReceiverHandler;
import org.androidannotations.internal.core.handler.RootContextHandler;
import org.androidannotations.internal.core.handler.SeekBarProgressChangeHandler;
import org.androidannotations.internal.core.handler.SeekBarTouchStartHandler;
import org.androidannotations.internal.core.handler.SeekBarTouchStopHandler;
import org.androidannotations.internal.core.handler.ServiceActionHandler;
import org.androidannotations.internal.core.handler.SharedPrefHandler;
import org.androidannotations.internal.core.handler.SupposeBackgroundHandler;
import org.androidannotations.internal.core.handler.SupposeUiThreadHandler;
import org.androidannotations.internal.core.handler.SystemServiceHandler;
import org.androidannotations.internal.core.handler.TextChangeHandler;
import org.androidannotations.internal.core.handler.TouchHandler;
import org.androidannotations.internal.core.handler.TraceHandler;
import org.androidannotations.internal.core.handler.TransactionalHandler;
import org.androidannotations.internal.core.handler.UiThreadHandler;
import org.androidannotations.internal.core.handler.ViewByIdHandler;
import org.androidannotations.internal.core.handler.ViewsByIdHandler;
import org.androidannotations.internal.core.handler.WakeLockHandler;
import org.androidannotations.internal.core.handler.WindowFeatureHandler;
import org.androidannotations.internal.core.model.AndroidRes;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;

public class CorePlugin extends AndroidAnnotationsPlugin {

	private static final String NAME = "AndroidAnnotations";

	private static final Option OPTION_TRACE = new Option("trace", "false");
	private static final Option OPTION_THREAD_CONTROL = new Option("threadControl", "true");

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<Option> getSupportedOptions() {
		return Arrays.asList(OPTION_TRACE, OPTION_THREAD_CONTROL);
	}

	@Override
	public List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv) {
		List<AnnotationHandler<?>> annotationHandlers = new ArrayList<>();
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
			} else if (androidRes == AndroidRes.COLOR) {
				annotationHandlers.add(new ColorResHandler(androidAnnotationEnv));
			} else if (androidRes == AndroidRes.COLOR_STATE_LIST) {
				annotationHandlers.add(new ColorStateListResHandler(androidAnnotationEnv));
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
		annotationHandlers.add(new KeyDownHandler(androidAnnotationEnv));
		annotationHandlers.add(new KeyLongPressHandler(androidAnnotationEnv));
		annotationHandlers.add(new KeyMultipleHandler(androidAnnotationEnv));
		annotationHandlers.add(new KeyUpHandler(androidAnnotationEnv));
		annotationHandlers.add(new ServiceActionHandler(androidAnnotationEnv));
		annotationHandlers.add(new InstanceStateHandler(androidAnnotationEnv));
		annotationHandlers.add(new HttpsClientHandler(androidAnnotationEnv));
		annotationHandlers.add(new HierarchyViewerSupportHandler(androidAnnotationEnv));
		annotationHandlers.add(new WindowFeatureHandler(androidAnnotationEnv));
		annotationHandlers.add(new ReceiverHandler(androidAnnotationEnv));
		annotationHandlers.add(new ReceiverActionHandler(androidAnnotationEnv));
		annotationHandlers.add(new OnActivityResultHandler(androidAnnotationEnv));

		annotationHandlers.add(new IgnoredWhenDetachedHandler(androidAnnotationEnv));

		annotationHandlers.add(new AfterInjectHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterExtrasHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterViewsHandler(androidAnnotationEnv));

		annotationHandlers.add(new PreferenceScreenHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceHeadersHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceByKeyHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceChangeHandler(androidAnnotationEnv));
		annotationHandlers.add(new PreferenceClickHandler(androidAnnotationEnv));
		annotationHandlers.add(new AfterPreferencesHandler(androidAnnotationEnv));

		if (androidAnnotationEnv.getOptionBooleanValue(OPTION_TRACE)) {
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
		if (androidAnnotationEnv.getOptionBooleanValue(OPTION_THREAD_CONTROL)) {
			annotationHandlers.add(new SupposeUiThreadHandler(androidAnnotationEnv));
			annotationHandlers.add(new SupposeBackgroundHandler(androidAnnotationEnv));
		}

		return annotationHandlers;
	}
}
