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
package org.androidannotations;

import org.androidannotations.generation.CodeModelGenerator;
import org.androidannotations.handler.AnnotationHandlers;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AndroidManifestFinder;
import org.androidannotations.helper.Option;
import org.androidannotations.helper.TimeStats;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;
import org.androidannotations.model.ModelExtractor;
import org.androidannotations.process.ModelProcessor;
import org.androidannotations.process.ModelValidator;
import org.androidannotations.rclass.AndroidRClassFinder;
import org.androidannotations.rclass.CoumpoundRClass;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.ProjectRClassFinder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import static org.androidannotations.helper.AndroidManifestFinder.ANDROID_MANIFEST_FILE_OPTION;
import static org.androidannotations.helper.ModelConstants.TRACE_OPTION;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({ TRACE_OPTION, ANDROID_MANIFEST_FILE_OPTION })
public class AndroidAnnotationProcessor extends AbstractProcessor {

	private final TimeStats timeStats = new TimeStats();
	private AnnotationHandlers annotationHandlers;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		Messager messager = processingEnv.getMessager();
		timeStats.setMessager(messager);
		messager.printMessage(Diagnostic.Kind.NOTE, "Starting AndroidAnnotations annotation processing");

		annotationHandlers = new AnnotationHandlers(processingEnv);
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

		AnnotationElementsHolder extractedModel = extractAnnotations(annotations, roundEnv);

		Option<AndroidManifest> androidManifestOption = extractAndroidManifest();

		if (androidManifestOption.isAbsent()) {
			return;
		}

		AndroidManifest androidManifest = androidManifestOption.get();

		Option<IRClass> rClassOption = findRClasses(androidManifest);

		if (rClassOption.isAbsent()) {
			return;
		}

		IRClass rClass = rClassOption.get();

		AndroidSystemServices androidSystemServices = new AndroidSystemServices();

		annotationHandlers.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);

		AnnotationElements validatedModel = validateAnnotations(extractedModel);

		ModelProcessor.ProcessResult processResult = processAnnotations(validatedModel);

		generateSources(processResult);
	}

	private boolean nothingToDo(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return roundEnv.processingOver() || annotations.size() == 0;
	}

	private AnnotationElementsHolder extractAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.start("Extract Annotations");
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, getSupportedAnnotationTypes(), roundEnv);
		timeStats.stop("Extract Annotations");
		return extractedModel;
	}

	private Option<AndroidManifest> extractAndroidManifest() {
		timeStats.start("Extract Manifest");
		AndroidManifestFinder finder = new AndroidManifestFinder(processingEnv);
		Option<AndroidManifest> manifest = finder.extractAndroidManifest();
		timeStats.stop("Extract Manifest");
		return manifest;
	}

	private Option<IRClass> findRClasses(AndroidManifest androidManifest) throws IOException {
		timeStats.start("Find R Classes");
		ProjectRClassFinder rClassFinder = new ProjectRClassFinder(processingEnv);

		Option<IRClass> rClass = rClassFinder.find(androidManifest);

		AndroidRClassFinder androidRClassFinder = new AndroidRClassFinder(processingEnv);

		Option<IRClass> androidRClass = androidRClassFinder.find();

		if (rClass.isAbsent() || androidRClass.isAbsent()) {
			return Option.absent();
		}

		IRClass coumpoundRClass = new CoumpoundRClass(rClass.get(), androidRClass.get());

		timeStats.stop("Find R Classes");

		return Option.of(coumpoundRClass);
	}

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel) {
		timeStats.start("Validate Annotations");
		ModelValidator modelValidator = new ModelValidator(annotationHandlers);
		AnnotationElements validatedAnnotations = modelValidator.validate(extractedModel);
		timeStats.stop("Validate Annotations");
		return validatedAnnotations;
	}

	/*
	private ModelValidator buildModelValidator(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(new EApplicationValidator(processingEnv, androidManifest));
		modelValidator.register(new EActivityValidator(processingEnv, rClass, androidManifest));
		modelValidator.register(new EServiceValidator(processingEnv, androidManifest));
		modelValidator.register(new EReceiverValidator(processingEnv, androidManifest));
		modelValidator.register(new EProviderValidator(processingEnv, androidManifest));
		modelValidator.register(new EFragmentValidator(processingEnv, rClass));
		modelValidator.register(new EViewGroupValidator(processingEnv, rClass));
		modelValidator.register(new EViewValidator(processingEnv));
		modelValidator.register(new EBeanValidator(processingEnv));
		modelValidator.register(new RoboGuiceValidator(processingEnv));
		modelValidator.register(new ViewByIdValidator(processingEnv, rClass));
		modelValidator.register(new FragmentByIdValidator(processingEnv, rClass));
		modelValidator.register(new FragmentByTagValidator(processingEnv));
		modelValidator.register(new FromHtmlValidator(processingEnv, rClass));
		modelValidator.register(new ClickValidator(processingEnv, rClass));
		modelValidator.register(new LongClickValidator(processingEnv, rClass));
		modelValidator.register(new TouchValidator(processingEnv, rClass));
		modelValidator.register(new FocusChangeValidator(processingEnv, rClass));
		modelValidator.register(new CheckedChangeValidator(processingEnv, rClass));
		modelValidator.register(new ItemClickValidator(processingEnv, rClass));
		modelValidator.register(new ItemSelectedValidator(processingEnv, rClass));
		modelValidator.register(new ItemLongClickValidator(processingEnv, rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelValidator.register(new ResValidator(androidRes, processingEnv, rClass));
		}
		modelValidator.register(new TransactionalValidator(processingEnv));
		modelValidator.register(new ExtraValidator(processingEnv));
		modelValidator.register(new FragmentArgValidator(processingEnv));
		modelValidator.register(new SystemServiceValidator(processingEnv, androidSystemServices));
		modelValidator.register(new SharedPrefValidator(processingEnv));
		modelValidator.register(new PrefValidator(processingEnv));
		modelValidator.register(new RestValidator(processingEnv, androidManifest));
		modelValidator.register(new DeleteValidator(processingEnv));
		modelValidator.register(new GetValidator(processingEnv));
		modelValidator.register(new HeadValidator(processingEnv));
		modelValidator.register(new OptionsValidator(processingEnv));
		modelValidator.register(new PostValidator(processingEnv));
		modelValidator.register(new PutValidator(processingEnv));
		modelValidator.register(new AcceptValidator(processingEnv));
		modelValidator.register(new AppValidator(processingEnv));
		modelValidator.register(new OptionsMenuValidator(processingEnv, rClass));
		modelValidator.register(new OptionsMenuItemValidator(processingEnv, rClass));
		modelValidator.register(new OptionsItemValidator(processingEnv, rClass));
		modelValidator.register(new NoTitleValidator(processingEnv));
		modelValidator.register(new WindowFeatureValidator(processingEnv));
		modelValidator.register(new CustomTitleValidator(processingEnv, rClass));
		modelValidator.register(new FullscreenValidator(processingEnv));
		modelValidator.register(new RestServiceValidator(processingEnv));
		modelValidator.register(new RootContextValidator(processingEnv));
		modelValidator.register(new NonConfigurationInstanceValidator(processingEnv));
		modelValidator.register(new BeanValidator(processingEnv));
		modelValidator.register(new AfterInjectValidator(processingEnv));
		modelValidator.register(new BeforeTextChangeValidator(processingEnv, rClass));
		modelValidator.register(new TextChangeValidator(processingEnv, rClass));
		modelValidator.register(new AfterTextChangeValidator(processingEnv, rClass));
		modelValidator.register(new SeekBarProgressChangeValidator(processingEnv, rClass));
		modelValidator.register(new SeekBarTouchStartValidator(processingEnv, rClass));
		modelValidator.register(new SeekBarTouchStopValidator(processingEnv, rClass));
		/*
		 * Any view injection or listener binding should occur before
		 * AfterViewsValidator
		 *
		modelValidator.register(new AfterViewsValidator(processingEnv));
		modelValidator.register(new TraceValidator(processingEnv));
		modelValidator.register(new SubscribeValidator(processingEnv));
		modelValidator.register(new ProduceValidator(processingEnv));
		modelValidator.register(new RunnableValidator(UiThread.class.getName(), processingEnv));
		modelValidator.register(new RunnableValidator(Background.class.getName(), processingEnv));
		modelValidator.register(new InstanceStateValidator(processingEnv));
		modelValidator.register(new OrmLiteDaoValidator(processingEnv, rClass));
		modelValidator.register(new HttpsClientValidator(processingEnv, rClass));
		modelValidator.register(new OnActivityResultValidator(processingEnv, rClass));
		modelValidator.register(new HierarchyViewerSupportValidator(processingEnv, androidManifest));

		return modelValidator;
	}
	*/

	private boolean traceActivated() {
		Map<String, String> options = processingEnv.getOptions();
		if (options.containsKey(TRACE_OPTION)) {
			String trace = options.get(TRACE_OPTION);
			return !"false".equals(trace);
		} else {
			return true;
		}
	}

	private ModelProcessor.ProcessResult processAnnotations(AnnotationElements validatedModel) throws Exception {
		timeStats.start("Process Annotations");
		annotationHandlers.setValidatedModel(validatedModel);
		ModelProcessor modelProcessor = new ModelProcessor(processingEnv, annotationHandlers);
		ModelProcessor.ProcessResult processResult = modelProcessor.process(validatedModel);
		timeStats.stop("Process Annotations");
		return processResult;
	}

	/*
	private ModelProcessor buildModelProcessor(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest, AnnotationElements validatedModel) {
		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(new EApplicationProcessor());
		modelProcessor.register(new EActivityProcessor(processingEnv, rClass));
		modelProcessor.register(new EServiceProcessor());
		modelProcessor.register(new EReceiverProcessor());
		modelProcessor.register(new EProviderProcessor());
		modelProcessor.register(new EFragmentProcessor(processingEnv, rClass));
		modelProcessor.register(new EViewGroupProcessor(processingEnv, rClass));
		modelProcessor.register(new EViewProcessor());
		modelProcessor.register(new EBeanProcessor());
		modelProcessor.register(new SharedPrefProcessor(processingEnv, rClass));
		modelProcessor.register(new PrefProcessor(validatedModel));
		modelProcessor.register(new RoboGuiceProcessor());
		modelProcessor.register(new ViewByIdProcessor(processingEnv, rClass));
		modelProcessor.register(new FragmentByIdProcessor(processingEnv, rClass));
		modelProcessor.register(new FragmentByTagProcessor(processingEnv));
		modelProcessor.register(new FromHtmlProcessor(processingEnv, rClass));
		modelProcessor.register(new ClickProcessor(processingEnv, rClass));
		modelProcessor.register(new LongClickProcessor(processingEnv, rClass));
		modelProcessor.register(new TouchProcessor(processingEnv, rClass));
		modelProcessor.register(new FocusChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new CheckedChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new ItemClickProcessor(processingEnv, rClass));
		modelProcessor.register(new ItemSelectedProcessor(processingEnv, rClass));
		modelProcessor.register(new ItemLongClickProcessor(processingEnv, rClass));
		for (AndroidRes androidRes : AndroidRes.values()) {
			modelProcessor.register(new ResProcessor(processingEnv, androidRes, rClass));
		}
		modelProcessor.register(new TransactionalProcessor());
		modelProcessor.register(new ExtraProcessor(processingEnv));
		modelProcessor.register(new FragmentArgProcessor(processingEnv));
		modelProcessor.register(new SystemServiceProcessor(androidSystemServices));
		RestImplementationsHolder restImplementationsHolder = new RestImplementationsHolder();
		modelProcessor.register(new RestProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new GetProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new PostProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new PutProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new DeleteProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new HeadProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new OptionsProcessor(processingEnv, restImplementationsHolder));
		modelProcessor.register(new AppProcessor());
		modelProcessor.register(new OptionsMenuProcessor(processingEnv, rClass));
		modelProcessor.register(new OptionsMenuItemProcessor(processingEnv, rClass));
		modelProcessor.register(new OptionsItemProcessor(processingEnv, rClass));
		modelProcessor.register(new NoTitleProcessor());
		modelProcessor.register(new WindowFeatureProcessor());
		modelProcessor.register(new CustomTitleProcessor(processingEnv, rClass));
		modelProcessor.register(new FullscreenProcessor());
		modelProcessor.register(new RestServiceProcessor());
		modelProcessor.register(new OrmLiteDaoProcessor(processingEnv));
		modelProcessor.register(new RootContextProcessor());
		modelProcessor.register(new NonConfigurationInstanceProcessor(processingEnv));
		modelProcessor.register(new BeanProcessor(processingEnv));
		modelProcessor.register(new BeforeTextChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new TextChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new AfterTextChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new SeekBarProgressChangeProcessor(processingEnv, rClass));
		modelProcessor.register(new SeekBarTouchStartProcessor(processingEnv, rClass));
		modelProcessor.register(new SeekBarTouchStopProcessor(processingEnv, rClass));
		/*
		 * Any view injection or listener binding should occur before
		 * AfterViewsProcessor
		 *
		modelProcessor.register(new AfterViewsProcessor());
		if (traceActivated()) {
			modelProcessor.register(new TraceProcessor());
		}
		modelProcessor.register(new SubscribeProcessor());
		modelProcessor.register(new ProduceProcessor());
		modelProcessor.register(new UiThreadProcessor());
		modelProcessor.register(new BackgroundProcessor());
		modelProcessor.register(new AfterInjectProcessor());
		modelProcessor.register(new InstanceStateProcessor(processingEnv));
		modelProcessor.register(new HttpsClientProcessor(rClass));
		modelProcessor.register(new OnActivityResultProcessor(processingEnv, rClass));
		modelProcessor.register(new HierarchyViewerSupportProcessor());
		return modelProcessor;
	}
	*/

	private void generateSources(ModelProcessor.ProcessResult processResult) throws IOException {
		timeStats.start("Generate Sources");
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE, "Number of files generated by AndroidAnnotations: " + processResult.codeModel.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), messager);
		modelGenerator.generate(processResult);
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
		return writer.toString().replace("\n", "");
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return annotationHandlers.getSupportedAnnotationTypes();
	/*
		if (supportedAnnotationNames == null) {
			Class<?>[] annotationClassesArray = { //
			//
					EActivity.class, //
					App.class, //
					EViewGroup.class, //
					EView.class, //
					AfterViews.class, //
					RoboGuice.class, //
					ViewById.class, //
					Click.class, //
					LongClick.class, //
					ItemClick.class, //
					ItemLongClick.class, //
					Touch.class, //
					ItemSelect.class, //
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
					OptionsMenuItem.class, //
					OptionsItem.class, //
					HtmlRes.class, //
					NoTitle.class, //
					WindowFeature.class, //
					CustomTitle.class, //
					Fullscreen.class, //
					RestService.class, //
					EBean.class, //
					RootContext.class, //
					Bean.class, //
					AfterInject.class, //
					EService.class, //
					EReceiver.class, //
					EProvider.class, //
					Trace.class, //
					InstanceState.class, //
					NonConfigurationInstance.class, //
					EApplication.class, //
					EFragment.class, //
					FragmentById.class, //
					FragmentByTag.class, //
					BeforeTextChange.class, //
					TextChange.class, //
					SeekBarProgressChange.class, //
					SeekBarTouchStart.class, //
					SeekBarTouchStop.class, //
					AfterTextChange.class, //
					OrmLiteDao.class, //
					HttpsClient.class, //
					FragmentArg.class, //
					OnActivityResult.class, //
					HierarchyViewerSupport.class //
			};

			Set<String> set = new HashSet<String>(annotationClassesArray.length);
			for (Class<?> annotationClass : annotationClassesArray) {
				set.add(annotationClass.getName());
			}

			set.add(SUBSCRIBE);
			set.add(PRODUCE);

			supportedAnnotationNames = Collections.unmodifiableSet(set);
		}
		return supportedAnnotationNames;
	*/
	}
}
