/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
package com.googlecode.androidannotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.googlecode.androidannotations.annotationprocessor.AnnotatedAbstractProcessor;
import com.googlecode.androidannotations.annotationprocessor.SupportedAnnotationClasses;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.BeforeCreate;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.EProvider;
import com.googlecode.androidannotations.annotations.EReceiver;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.Enhanced;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.FromHtml;
import com.googlecode.androidannotations.annotations.Fullscreen;
import com.googlecode.androidannotations.annotations.Inject;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.ItemSelect;
import com.googlecode.androidannotations.annotations.LongClick;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.RoboGuice;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.SystemService;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.Trace;
import com.googlecode.androidannotations.annotations.Transactional;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.UiThreadDelayed;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.googlecode.androidannotations.annotations.res.BooleanRes;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.googlecode.androidannotations.annotations.res.ColorStateListRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelOffsetRes;
import com.googlecode.androidannotations.annotations.res.DimensionPixelSizeRes;
import com.googlecode.androidannotations.annotations.res.DimensionRes;
import com.googlecode.androidannotations.annotations.res.DrawableRes;
import com.googlecode.androidannotations.annotations.res.HtmlRes;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.googlecode.androidannotations.annotations.res.IntegerRes;
import com.googlecode.androidannotations.annotations.res.LayoutRes;
import com.googlecode.androidannotations.annotations.res.MovieRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.res.TextArrayRes;
import com.googlecode.androidannotations.annotations.res.TextRes;
import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Delete;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.annotations.rest.Options;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Put;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.generation.CodeModelGenerator;
import com.googlecode.androidannotations.helper.AndroidManifest;
import com.googlecode.androidannotations.helper.AndroidManifestFinder;
import com.googlecode.androidannotations.helper.TimeStats;
import com.googlecode.androidannotations.model.AndroidRes;
import com.googlecode.androidannotations.model.AndroidSystemServices;
import com.googlecode.androidannotations.model.AnnotationElements;
import com.googlecode.androidannotations.model.AnnotationElementsHolder;
import com.googlecode.androidannotations.model.EmptyAnnotationElements;
import com.googlecode.androidannotations.model.ModelExtractor;
import com.googlecode.androidannotations.processing.AfterInjectProcessor;
import com.googlecode.androidannotations.processing.AfterViewsProcessor;
import com.googlecode.androidannotations.processing.AppProcessor;
import com.googlecode.androidannotations.processing.BackgroundProcessor;
import com.googlecode.androidannotations.processing.BeforeCreateProcessor;
import com.googlecode.androidannotations.processing.ClickProcessor;
import com.googlecode.androidannotations.processing.EActivityProcessor;
import com.googlecode.androidannotations.processing.EApplicationProcessor;
import com.googlecode.androidannotations.processing.EProviderProcessor;
import com.googlecode.androidannotations.processing.EReceiverProcessor;
import com.googlecode.androidannotations.processing.EServiceProcessor;
import com.googlecode.androidannotations.processing.EViewGroupProcessor;
import com.googlecode.androidannotations.processing.EnhancedProcessor;
import com.googlecode.androidannotations.processing.ExtraProcessor;
import com.googlecode.androidannotations.processing.FromHtmlProcessor;
import com.googlecode.androidannotations.processing.FullscreenProcessor;
import com.googlecode.androidannotations.processing.InjectProcessor;
import com.googlecode.androidannotations.processing.ItemClickProcessor;
import com.googlecode.androidannotations.processing.ItemLongClickProcessor;
import com.googlecode.androidannotations.processing.ItemSelectedProcessor;
import com.googlecode.androidannotations.processing.LongClickProcessor;
import com.googlecode.androidannotations.processing.ModelProcessor;
import com.googlecode.androidannotations.processing.NoTitleProcessor;
import com.googlecode.androidannotations.processing.OptionsItemProcessor;
import com.googlecode.androidannotations.processing.OptionsMenuProcessor;
import com.googlecode.androidannotations.processing.PrefProcessor;
import com.googlecode.androidannotations.processing.ResProcessor;
import com.googlecode.androidannotations.processing.RestServiceProcessor;
import com.googlecode.androidannotations.processing.RoboGuiceProcessor;
import com.googlecode.androidannotations.processing.RootContextProcessor;
import com.googlecode.androidannotations.processing.SharedPrefProcessor;
import com.googlecode.androidannotations.processing.SystemServiceProcessor;
import com.googlecode.androidannotations.processing.TouchProcessor;
import com.googlecode.androidannotations.processing.TraceProcessor;
import com.googlecode.androidannotations.processing.TransactionalProcessor;
import com.googlecode.androidannotations.processing.UiThreadDelayedProcessor;
import com.googlecode.androidannotations.processing.UiThreadProcessor;
import com.googlecode.androidannotations.processing.ViewByIdProcessor;
import com.googlecode.androidannotations.processing.rest.DeleteProcessor;
import com.googlecode.androidannotations.processing.rest.GetProcessor;
import com.googlecode.androidannotations.processing.rest.HeadProcessor;
import com.googlecode.androidannotations.processing.rest.OptionsProcessor;
import com.googlecode.androidannotations.processing.rest.PostProcessor;
import com.googlecode.androidannotations.processing.rest.PutProcessor;
import com.googlecode.androidannotations.processing.rest.RestImplementationsHolder;
import com.googlecode.androidannotations.processing.rest.RestProcessor;
import com.googlecode.androidannotations.rclass.AndroidRClassFinder;
import com.googlecode.androidannotations.rclass.CoumpoundRClass;
import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.ProjectRClassFinder;
import com.googlecode.androidannotations.validation.AfterInjectValidator;
import com.googlecode.androidannotations.validation.AfterViewsValidator;
import com.googlecode.androidannotations.validation.AppValidator;
import com.googlecode.androidannotations.validation.BeforeCreateValidator;
import com.googlecode.androidannotations.validation.ClickValidator;
import com.googlecode.androidannotations.validation.EActivityValidator;
import com.googlecode.androidannotations.validation.EApplicationValidator;
import com.googlecode.androidannotations.validation.EProviderValidator;
import com.googlecode.androidannotations.validation.EReceiverValidator;
import com.googlecode.androidannotations.validation.EServiceValidator;
import com.googlecode.androidannotations.validation.EViewGroupValidator;
import com.googlecode.androidannotations.validation.EnhancedValidator;
import com.googlecode.androidannotations.validation.ExtraValidator;
import com.googlecode.androidannotations.validation.FromHtmlValidator;
import com.googlecode.androidannotations.validation.FullscreenValidator;
import com.googlecode.androidannotations.validation.InjectValidator;
import com.googlecode.androidannotations.validation.ItemClickValidator;
import com.googlecode.androidannotations.validation.ItemLongClickValidator;
import com.googlecode.androidannotations.validation.ItemSelectedValidator;
import com.googlecode.androidannotations.validation.LongClickValidator;
import com.googlecode.androidannotations.validation.ModelValidator;
import com.googlecode.androidannotations.validation.NoTitleValidator;
import com.googlecode.androidannotations.validation.OptionsItemValidator;
import com.googlecode.androidannotations.validation.OptionsMenuValidator;
import com.googlecode.androidannotations.validation.PrefValidator;
import com.googlecode.androidannotations.validation.ResValidator;
import com.googlecode.androidannotations.validation.RestServiceValidator;
import com.googlecode.androidannotations.validation.RoboGuiceValidator;
import com.googlecode.androidannotations.validation.RootContextValidator;
import com.googlecode.androidannotations.validation.RunnableValidator;
import com.googlecode.androidannotations.validation.SharedPrefValidator;
import com.googlecode.androidannotations.validation.SystemServiceValidator;
import com.googlecode.androidannotations.validation.TouchValidator;
import com.googlecode.androidannotations.validation.TraceValidator;
import com.googlecode.androidannotations.validation.TransactionalValidator;
import com.googlecode.androidannotations.validation.ViewByIdValidator;
import com.googlecode.androidannotations.validation.rest.AcceptValidator;
import com.googlecode.androidannotations.validation.rest.DeleteValidator;
import com.googlecode.androidannotations.validation.rest.GetValidator;
import com.googlecode.androidannotations.validation.rest.HeadValidator;
import com.googlecode.androidannotations.validation.rest.OptionsValidator;
import com.googlecode.androidannotations.validation.rest.PostValidator;
import com.googlecode.androidannotations.validation.rest.PutValidator;
import com.googlecode.androidannotations.validation.rest.RestValidator;
import com.sun.codemodel.JCodeModel;

@SupportedAnnotationClasses({ EActivity.class, //
		App.class, //
		BeforeCreate.class, //
		EViewGroup.class, //
		AfterViews.class, //
		RoboGuice.class, //
		ViewById.class, //
		Click.class, //
		LongClick.class, //
		ItemClick.class, //
		ItemLongClick.class, //
		Touch.class, //
		ItemSelect.class, //
		UiThreadDelayed.class, //
		UiThread.class, //
		Transactional.class, //
		Background.class, //
		Extra.class, //
		SystemService.class, //
		SharedPref.class, //
		Pref.class, //
		StringRes.class, //
		ColorRes.class, //
		AnimationRes.class, //
		BooleanRes.class, //
		ColorStateListRes.class, //
		DimensionRes.class, //
		DimensionPixelOffsetRes.class, //
		DimensionPixelSizeRes.class, //
		DrawableRes.class, //
		IntArrayRes.class, //
		IntegerRes.class, //
		LayoutRes.class, //
		MovieRes.class, //
		TextRes.class, //
		TextArrayRes.class, //
		StringArrayRes.class, //
		Rest.class, //
		Get.class, //
		Head.class, //
		Options.class, //
		Post.class, //
		Put.class, //
		Delete.class, //
		Accept.class, //
		FromHtml.class, //
		OptionsMenu.class, //
		OptionsItem.class, //
		HtmlRes.class, //
		NoTitle.class, //
		Fullscreen.class, //
		RestService.class, //
		Enhanced.class, //
		RootContext.class, //
		Inject.class, //
		AfterInject.class, //
		EService.class, //
		EReceiver.class, //
		EProvider.class, //
		EApplication.class, //
		Trace.class //
})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AndroidAnnotationProcessor extends AnnotatedAbstractProcessor {

	/**
	 * We do not need multiple round processing, since the generated classes do
	 * not need to be processed.
	 */
	private boolean alreadyProcessed = false;

	private final TimeStats timeStats = new TimeStats();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		Messager messager = processingEnv.getMessager();

		timeStats.setMessager(messager);

		messager.printMessage(Diagnostic.Kind.NOTE, "Starting AndroidAnnotations annotation processing");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.clear();
		timeStats.start("Whole Processing");
		try {
			processThrowing(annotations, roundEnv);
		} catch (Exception e) {
			handleException(annotations, roundEnv, e);
		}
		timeStats.stop("Whole Processing");
		timeStats.logStats();
		return true;
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {

		if (nothingToDo(annotations, roundEnv)) {
			return;
		}

		alreadyProcessed = true;

		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);

		AndroidManifest androidManifest = extractAndroidManifest();

		IRClass rClass = findRClasses(androidManifest);

		AndroidSystemServices androidSystemServices = new AndroidSystemServices();

		AnnotationElements validatedModel = validateAnnotations(extractedModel, rClass, androidSystemServices, androidManifest);

		JCodeModel codeModel = processAnnotations(validatedModel, rClass, androidSystemServices, androidManifest);

		generateSources(codeModel);
	}

	private boolean nothingToDo(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return roundEnv.processingOver() || annotations.size() == 0 || alreadyProcessed;
	}

	private AnnotationElementsHolder extractAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.start("Extract Annotations");
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, roundEnv);
		timeStats.stop("Extract Annotations");
		return extractedModel;
	}

	private AndroidManifest extractAndroidManifest() {
		timeStats.start("Extract Manifest");
		AndroidManifestFinder finder = new AndroidManifestFinder(processingEnv);
		AndroidManifest manifest = finder.extractAndroidManifest();
		timeStats.stop("Extract Manifest");
		return manifest;
	}

	private IRClass findRClasses(AndroidManifest androidManifest) throws IOException {
		timeStats.start("Find R Classes");
		ProjectRClassFinder rClassFinder = new ProjectRClassFinder(processingEnv);
		IRClass rClass = rClassFinder.find(androidManifest);

		AndroidRClassFinder androidRClassFinder = new AndroidRClassFinder(processingEnv);

		IRClass androidRClass = androidRClassFinder.find();

		CoumpoundRClass coumpoundRClass = new CoumpoundRClass(rClass, androidRClass);

		timeStats.stop("Find R Classes");

		return coumpoundRClass;
	}

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel, IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		timeStats.start("Validate Annotations");
		AnnotationElements validatedAnnotations;
		if (rClass != null) {
			ModelValidator modelValidator = buildModelValidator(rClass, androidSystemServices, androidManifest);
			validatedAnnotations = modelValidator.validate(extractedModel);
		} else {
			validatedAnnotations = EmptyAnnotationElements.INSTANCE;
		}
		timeStats.stop("Validate Annotations");
		return validatedAnnotations;
	}

	private ModelValidator buildModelValidator(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(new EApplicationValidator(processingEnv, androidManifest));
		modelValidator.register(new EActivityValidator(processingEnv, rClass, androidManifest));
		modelValidator.register(new EServiceValidator(processingEnv, androidManifest));
		modelValidator.register(new EReceiverValidator(processingEnv, androidManifest));
		modelValidator.register(new EProviderValidator(processingEnv, androidManifest));
		modelValidator.register(new EViewGroupValidator(processingEnv, rClass));
		modelValidator.register(new EnhancedValidator(processingEnv));
		modelValidator.register(new RoboGuiceValidator(processingEnv));
		modelValidator.register(new ViewByIdValidator(processingEnv, rClass));
		modelValidator.register(new FromHtmlValidator(processingEnv, rClass));
		modelValidator.register(new ClickValidator(processingEnv, rClass));
		modelValidator.register(new LongClickValidator(processingEnv, rClass));
		modelValidator.register(new TouchValidator(processingEnv, rClass));
		modelValidator.register(new ItemClickValidator(processingEnv, rClass));
		modelValidator.register(new ItemSelectedValidator(processingEnv, rClass));
		modelValidator.register(new ItemLongClickValidator(processingEnv, rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelValidator.register(new ResValidator(androidRes, processingEnv, rClass));
		}
		modelValidator.register(new TransactionalValidator(processingEnv));
		modelValidator.register(new ExtraValidator(processingEnv));
		modelValidator.register(new SystemServiceValidator(processingEnv, androidSystemServices));
		modelValidator.register(new BeforeCreateValidator(processingEnv));
		modelValidator.register(new AfterViewsValidator(processingEnv));
		modelValidator.register(new SharedPrefValidator(processingEnv));
		modelValidator.register(new PrefValidator(processingEnv));
		modelValidator.register(new RestValidator(processingEnv));
		modelValidator.register(new DeleteValidator(processingEnv));
		modelValidator.register(new GetValidator(processingEnv));
		modelValidator.register(new HeadValidator(processingEnv));
		modelValidator.register(new OptionsValidator(processingEnv));
		modelValidator.register(new PostValidator(processingEnv));
		modelValidator.register(new PutValidator(processingEnv));
		modelValidator.register(new AcceptValidator(processingEnv));
		modelValidator.register(new AppValidator(processingEnv, androidManifest));
		modelValidator.register(new OptionsMenuValidator(processingEnv, rClass));
		modelValidator.register(new OptionsItemValidator(processingEnv, rClass));
		modelValidator.register(new NoTitleValidator(processingEnv));
		modelValidator.register(new FullscreenValidator(processingEnv));
		modelValidator.register(new RestServiceValidator(processingEnv));
		modelValidator.register(new RootContextValidator(processingEnv));
		modelValidator.register(new InjectValidator(processingEnv));
		modelValidator.register(new AfterInjectValidator(processingEnv));
		if (traceActivated()) {
			modelValidator.register(new TraceValidator(processingEnv));
		}
		modelValidator.register(new RunnableValidator(UiThreadDelayed.class, processingEnv));
		modelValidator.register(new RunnableValidator(UiThread.class, processingEnv));
		modelValidator.register(new RunnableValidator(Background.class, processingEnv));
		return modelValidator;
	}

	private boolean traceActivated() {
		Map<String, String> options = processingEnv.getOptions();
		if (options.containsKey("trace")) {
			String trace = options.get("trace");
			return !"false".equals(trace);
		} else {
			return true;
		}
	}

	private JCodeModel processAnnotations(AnnotationElements validatedModel, IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) throws Exception {
		timeStats.start("Process Annotations");
		ModelProcessor modelProcessor = buildModelProcessor(rClass, androidSystemServices, androidManifest, validatedModel);
		JCodeModel codeModel = modelProcessor.process(validatedModel);
		timeStats.stop("Process Annotations");
		return codeModel;
	}

	private ModelProcessor buildModelProcessor(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest, AnnotationElements validatedModel) {
		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(new EApplicationProcessor(processingEnv));
		modelProcessor.register(new EActivityProcessor(processingEnv, rClass));
		modelProcessor.register(new EServiceProcessor(processingEnv));
		modelProcessor.register(new EReceiverProcessor(processingEnv));
		modelProcessor.register(new EProviderProcessor(processingEnv));
		modelProcessor.register(new EViewGroupProcessor(processingEnv, rClass));
		modelProcessor.register(new EnhancedProcessor(processingEnv));
		modelProcessor.register(new SharedPrefProcessor(processingEnv));
		modelProcessor.register(new PrefProcessor(validatedModel));
		modelProcessor.register(new RoboGuiceProcessor());
		modelProcessor.register(new ViewByIdProcessor(rClass));
		modelProcessor.register(new FromHtmlProcessor(rClass));
		modelProcessor.register(new ClickProcessor(rClass));
		modelProcessor.register(new LongClickProcessor(rClass));
		modelProcessor.register(new TouchProcessor(rClass));
		modelProcessor.register(new ItemClickProcessor(rClass));
		modelProcessor.register(new ItemSelectedProcessor(rClass));
		modelProcessor.register(new ItemLongClickProcessor(rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelProcessor.register(new ResProcessor(androidRes, rClass));
		}
		modelProcessor.register(new TransactionalProcessor());
		modelProcessor.register(new ExtraProcessor(processingEnv));
		modelProcessor.register(new SystemServiceProcessor(androidSystemServices));
		modelProcessor.register(new BeforeCreateProcessor());
		modelProcessor.register(new AfterViewsProcessor());
		RestImplementationsHolder restImplementationHolder = new RestImplementationsHolder();
		modelProcessor.register(new RestProcessor(restImplementationHolder));
		modelProcessor.register(new GetProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new PostProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new PutProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new DeleteProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new HeadProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new OptionsProcessor(processingEnv, restImplementationHolder));
		modelProcessor.register(new AppProcessor());
		modelProcessor.register(new OptionsMenuProcessor(rClass));
		modelProcessor.register(new OptionsItemProcessor(rClass));
		modelProcessor.register(new NoTitleProcessor());
		modelProcessor.register(new FullscreenProcessor());
		modelProcessor.register(new RestServiceProcessor());
		modelProcessor.register(new RootContextProcessor());
		modelProcessor.register(new InjectProcessor());
		modelProcessor.register(new TraceProcessor());
		modelProcessor.register(new UiThreadProcessor());
		modelProcessor.register(new UiThreadDelayedProcessor());
		modelProcessor.register(new BackgroundProcessor());
		modelProcessor.register(new AfterInjectProcessor());
		return modelProcessor;
	}

	private void generateSources(JCodeModel model) throws IOException {
		timeStats.start("Generate Sources");
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE, "Number of files generated by AndroidAnnotations: " + model.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), messager);
		modelGenerator.generate(model);
		timeStats.stop("Generate Sources");
	}

	private void handleException(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, Exception e) {
		String errorMessage = "Unexpected error. Please report an issue on AndroidAnnotations, with the following content: " + stackTraceToString(e);

		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.ERROR, errorMessage);

		/*
		 * Printing exception as an error on a random element. The exception is
		 * not related to this element, but otherwise it wouldn't show up in
		 * eclipse.
		 */

		Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
		messager.printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
	}

	private String stackTraceToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		e.printStackTrace(pw);
		return writer.toString();
	}
}