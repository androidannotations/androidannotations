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

import static org.androidannotations.helper.AndroidManifestFinder.ANDROID_MANIFEST_FILE_OPTION;
import static org.androidannotations.helper.CanonicalNameConstants.PRODUCE;
import static org.androidannotations.helper.CanonicalNameConstants.SUBSCRIBE;
import static org.androidannotations.helper.ModelConstants.TRACE_OPTION;
import static org.androidannotations.rclass.ProjectRClassFinder.RESOURCE_PACKAGE_NAME_OPTION;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.BeforeTextChange;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.CustomTitle;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.EProvider;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.FromHtml;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.HierarchyViewerSupport;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RoboGuice;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.SeekBarTouchStart;
import org.androidannotations.annotations.SeekBarTouchStop;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.Transactional;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.BooleanRes;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.ColorStateListRes;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.annotations.res.IntArrayRes;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.LayoutRes;
import org.androidannotations.annotations.res.MovieRes;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.res.TextArrayRes;
import org.androidannotations.annotations.res.TextRes;
import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Head;
import org.androidannotations.annotations.rest.Options;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.exception.ProcessingException;
import org.androidannotations.exception.VersionMismatchException;
import org.androidannotations.generation.CodeModelGenerator;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.AndroidManifestFinder;
import org.androidannotations.helper.ErrorHelper;
import org.androidannotations.helper.Option;
import org.androidannotations.helper.TimeStats;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.model.AnnotationElementsHolder;
import org.androidannotations.model.ModelExtractor;
import org.androidannotations.processing.AfterInjectProcessor;
import org.androidannotations.processing.AfterTextChangeProcessor;
import org.androidannotations.processing.AfterViewsProcessor;
import org.androidannotations.processing.AppProcessor;
import org.androidannotations.processing.BackgroundProcessor;
import org.androidannotations.processing.BeanProcessor;
import org.androidannotations.processing.BeforeTextChangeProcessor;
import org.androidannotations.processing.CheckedChangeProcessor;
import org.androidannotations.processing.ClickProcessor;
import org.androidannotations.processing.CustomTitleProcessor;
import org.androidannotations.processing.EActivityProcessor;
import org.androidannotations.processing.EApplicationProcessor;
import org.androidannotations.processing.EBeanProcessor;
import org.androidannotations.processing.EFragmentProcessor;
import org.androidannotations.processing.EIntentServiceProcessor;
import org.androidannotations.processing.EProviderProcessor;
import org.androidannotations.processing.EReceiverProcessor;
import org.androidannotations.processing.EServiceProcessor;
import org.androidannotations.processing.EViewGroupProcessor;
import org.androidannotations.processing.EViewProcessor;
import org.androidannotations.processing.ExtraProcessor;
import org.androidannotations.processing.FocusChangeProcessor;
import org.androidannotations.processing.FragmentArgProcessor;
import org.androidannotations.processing.FragmentByIdProcessor;
import org.androidannotations.processing.FragmentByTagProcessor;
import org.androidannotations.processing.FromHtmlProcessor;
import org.androidannotations.processing.FullscreenProcessor;
import org.androidannotations.processing.HierarchyViewerSupportProcessor;
import org.androidannotations.processing.HttpsClientProcessor;
import org.androidannotations.processing.InstanceStateProcessor;
import org.androidannotations.processing.ItemClickProcessor;
import org.androidannotations.processing.ItemLongClickProcessor;
import org.androidannotations.processing.ItemSelectedProcessor;
import org.androidannotations.processing.LongClickProcessor;
import org.androidannotations.processing.ModelProcessor;
import org.androidannotations.processing.ModelProcessor.ProcessResult;
import org.androidannotations.processing.NoTitleProcessor;
import org.androidannotations.processing.NonConfigurationInstanceProcessor;
import org.androidannotations.processing.OnActivityResultProcessor;
import org.androidannotations.processing.OptionsItemProcessor;
import org.androidannotations.processing.OptionsMenuItemProcessor;
import org.androidannotations.processing.OptionsMenuProcessor;
import org.androidannotations.processing.OrmLiteDaoProcessor;
import org.androidannotations.processing.PrefProcessor;
import org.androidannotations.processing.ProduceProcessor;
import org.androidannotations.processing.ResProcessor;
import org.androidannotations.processing.RestServiceProcessor;
import org.androidannotations.processing.RoboGuiceProcessor;
import org.androidannotations.processing.RootContextProcessor;
import org.androidannotations.processing.SeekBarProgressChangeProcessor;
import org.androidannotations.processing.SeekBarTouchStartProcessor;
import org.androidannotations.processing.SeekBarTouchStopProcessor;
import org.androidannotations.processing.ServiceActionProcessor;
import org.androidannotations.processing.SharedPrefProcessor;
import org.androidannotations.processing.SubscribeProcessor;
import org.androidannotations.processing.SystemServiceProcessor;
import org.androidannotations.processing.TextChangeProcessor;
import org.androidannotations.processing.TouchProcessor;
import org.androidannotations.processing.TraceProcessor;
import org.androidannotations.processing.TransactionalProcessor;
import org.androidannotations.processing.UiThreadProcessor;
import org.androidannotations.processing.ViewByIdProcessor;
import org.androidannotations.processing.WindowFeatureProcessor;
import org.androidannotations.processing.rest.DeleteProcessor;
import org.androidannotations.processing.rest.GetProcessor;
import org.androidannotations.processing.rest.HeadProcessor;
import org.androidannotations.processing.rest.OptionsProcessor;
import org.androidannotations.processing.rest.PostProcessor;
import org.androidannotations.processing.rest.PutProcessor;
import org.androidannotations.processing.rest.RestImplementationsHolder;
import org.androidannotations.processing.rest.RestProcessor;
import org.androidannotations.rclass.AndroidRClassFinder;
import org.androidannotations.rclass.CoumpoundRClass;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.ProjectRClassFinder;
import org.androidannotations.validation.AfterInjectValidator;
import org.androidannotations.validation.AfterTextChangeValidator;
import org.androidannotations.validation.AfterViewsValidator;
import org.androidannotations.validation.AppValidator;
import org.androidannotations.validation.BeanValidator;
import org.androidannotations.validation.BeforeTextChangeValidator;
import org.androidannotations.validation.CheckedChangeValidator;
import org.androidannotations.validation.ClickValidator;
import org.androidannotations.validation.CustomTitleValidator;
import org.androidannotations.validation.EActivityValidator;
import org.androidannotations.validation.EApplicationValidator;
import org.androidannotations.validation.EBeanValidator;
import org.androidannotations.validation.EFragmentValidator;
import org.androidannotations.validation.EIntentServiceValidator;
import org.androidannotations.validation.EProviderValidator;
import org.androidannotations.validation.EReceiverValidator;
import org.androidannotations.validation.EServiceValidator;
import org.androidannotations.validation.EViewGroupValidator;
import org.androidannotations.validation.EViewValidator;
import org.androidannotations.validation.ExtraValidator;
import org.androidannotations.validation.FocusChangeValidator;
import org.androidannotations.validation.FragmentArgValidator;
import org.androidannotations.validation.FragmentByIdValidator;
import org.androidannotations.validation.FragmentByTagValidator;
import org.androidannotations.validation.FromHtmlValidator;
import org.androidannotations.validation.FullscreenValidator;
import org.androidannotations.validation.HierarchyViewerSupportValidator;
import org.androidannotations.validation.HttpsClientValidator;
import org.androidannotations.validation.InstanceStateValidator;
import org.androidannotations.validation.ItemClickValidator;
import org.androidannotations.validation.ItemLongClickValidator;
import org.androidannotations.validation.ItemSelectedValidator;
import org.androidannotations.validation.LongClickValidator;
import org.androidannotations.validation.ModelValidator;
import org.androidannotations.validation.NoTitleValidator;
import org.androidannotations.validation.NonConfigurationInstanceValidator;
import org.androidannotations.validation.OnActivityResultValidator;
import org.androidannotations.validation.OptionsItemValidator;
import org.androidannotations.validation.OptionsMenuItemValidator;
import org.androidannotations.validation.OptionsMenuValidator;
import org.androidannotations.validation.OrmLiteDaoValidator;
import org.androidannotations.validation.PrefValidator;
import org.androidannotations.validation.ProduceValidator;
import org.androidannotations.validation.ResValidator;
import org.androidannotations.validation.RestServiceValidator;
import org.androidannotations.validation.RoboGuiceValidator;
import org.androidannotations.validation.RootContextValidator;
import org.androidannotations.validation.RunnableValidator;
import org.androidannotations.validation.SeekBarProgressChangeValidator;
import org.androidannotations.validation.SeekBarTouchStartValidator;
import org.androidannotations.validation.SeekBarTouchStopValidator;
import org.androidannotations.validation.ServiceActionValidator;
import org.androidannotations.validation.SharedPrefValidator;
import org.androidannotations.validation.SubscribeValidator;
import org.androidannotations.validation.SystemServiceValidator;
import org.androidannotations.validation.TextChangeValidator;
import org.androidannotations.validation.TouchValidator;
import org.androidannotations.validation.TraceValidator;
import org.androidannotations.validation.TransactionalValidator;
import org.androidannotations.validation.ViewByIdValidator;
import org.androidannotations.validation.WindowFeatureValidator;
import org.androidannotations.validation.rest.AcceptValidator;
import org.androidannotations.validation.rest.DeleteValidator;
import org.androidannotations.validation.rest.GetValidator;
import org.androidannotations.validation.rest.HeadValidator;
import org.androidannotations.validation.rest.OptionsValidator;
import org.androidannotations.validation.rest.PostValidator;
import org.androidannotations.validation.rest.PutValidator;
import org.androidannotations.validation.rest.RestValidator;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({ TRACE_OPTION, ANDROID_MANIFEST_FILE_OPTION, RESOURCE_PACKAGE_NAME_OPTION })
public class AndroidAnnotationProcessor extends AbstractProcessor {

	private final Properties properties = new Properties();
	private final Properties propertiesApi = new Properties();
	private final TimeStats timeStats = new TimeStats();
	private final ErrorHelper errorHelper = new ErrorHelper();

	private Set<String> supportedAnnotationNames;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		Messager messager = processingEnv.getMessager();

		try {
			loadPropertyFile();
			loadApiPropertyFile();
		} catch (Exception e) {
			messager.printMessage(Diagnostic.Kind.ERROR, "AndroidAnnotations processing failed: " + e.getMessage());
			throw new RuntimeException("AndroidAnnotations processing failed", e);
		}

		timeStats.setMessager(messager);

		messager.printMessage(Diagnostic.Kind.NOTE, "Starting AndroidAnnotations annotation processing");

	}

	private void checkApiAndCoreVersions() throws VersionMismatchException {
		String apiVersion = getAAApiVersion();
		String coreVersion = getAAProcessorVersion();

		if (!apiVersion.equals(coreVersion)) {
			throw new VersionMismatchException("AndroidAnnotation version for API (" + apiVersion + ") and core (" + coreVersion + ") doesn't match. Please check your classpath");
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		timeStats.clear();
		timeStats.start("Whole Processing");

		try {
			checkApiAndCoreVersions();
			processThrowing(annotations, roundEnv);
		} catch (ProcessingException e) {
			handleException(annotations, roundEnv, e);
		} catch (Exception e) {
			handleException(annotations, roundEnv, new ProcessingException(e, null));
		}
		timeStats.stop("Whole Processing");
		timeStats.logStats();
		return true;
	}

	private void loadPropertyFile() throws FileNotFoundException {
		String filename = "androidannotations.properties";
		try {
			URL url = getClass().getClassLoader().getResource(filename);
			properties.load(url.openStream());
		} catch (Exception e) {
			throw new FileNotFoundException(filename + " couldn't be parsed.");
		}
	}

	private void loadApiPropertyFile() throws FileNotFoundException {
		String filename = "androidannotations-api.properties";
		try {
			URL url = EActivity.class.getClassLoader().getResource(filename);
			propertiesApi.load(url.openStream());
		} catch (Exception e) {
			throw new FileNotFoundException(filename + " couldn't be parsed. Please check your classpath and verify that AA-API's version is at least 3.0");
		}
	}

	private String getAAProcessorVersion() {
		return properties.getProperty("version", "3.0+");
	}

	private String getAAApiVersion() {
		return propertiesApi.getProperty("version", null);
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws ProcessingException, Exception {
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

		AnnotationElements validatedModel = validateAnnotations(extractedModel, rClass, androidSystemServices, androidManifest);

		ProcessResult processResult = processAnnotations(validatedModel, rClass, androidSystemServices, androidManifest);

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

	private AnnotationElements validateAnnotations(AnnotationElementsHolder extractedModel, IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) throws ProcessingException, Exception {
		timeStats.start("Validate Annotations");
		ModelValidator modelValidator = buildModelValidator(rClass, androidSystemServices, androidManifest);
		AnnotationElements validatedAnnotations = modelValidator.validate(extractedModel);
		timeStats.stop("Validate Annotations");
		return validatedAnnotations;
	}

	private ModelValidator buildModelValidator(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(new EApplicationValidator(processingEnv, androidManifest));
		modelValidator.register(new EActivityValidator(processingEnv, rClass, androidManifest));
		modelValidator.register(new EServiceValidator(processingEnv, androidManifest));
		modelValidator.register(new EIntentServiceValidator(processingEnv, androidManifest));
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
		modelValidator.register(new ServiceActionValidator(processingEnv));
		/*
		 * Any view injection or listener binding should occur before
		 * AfterViewsValidator
		 */
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

	private boolean traceActivated() {
		Map<String, String> options = processingEnv.getOptions();
		if (options.containsKey(TRACE_OPTION)) {
			String trace = options.get(TRACE_OPTION);
			return !"false".equals(trace);
		} else {
			return true;
		}
	}

	private ProcessResult processAnnotations(AnnotationElements validatedModel, IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) throws ProcessingException, Exception {
		timeStats.start("Process Annotations");
		ModelProcessor modelProcessor = buildModelProcessor(rClass, androidSystemServices, androidManifest, validatedModel);
		ProcessResult processResult = modelProcessor.process(validatedModel);
		timeStats.stop("Process Annotations");
		return processResult;
	}

	private ModelProcessor buildModelProcessor(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest, AnnotationElements validatedModel) {
		ModelProcessor modelProcessor = new ModelProcessor();
		modelProcessor.register(new EApplicationProcessor());
		modelProcessor.register(new EActivityProcessor(processingEnv, rClass));
		modelProcessor.register(new EServiceProcessor(processingEnv));
		modelProcessor.register(new EIntentServiceProcessor(processingEnv));
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
		modelProcessor.register(new ServiceActionProcessor(processingEnv));
		/*
		 * Any view injection or listener binding should occur before
		 * AfterViewsProcessor
		 */
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

	private void generateSources(ProcessResult processResult) throws IOException {
		timeStats.start("Generate Sources");
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE, "Number of files generated by AndroidAnnotations: " + processResult.codeModel.countArtifacts());
		CodeModelGenerator modelGenerator = new CodeModelGenerator(processingEnv.getFiler(), messager, getAAProcessorVersion());
		modelGenerator.generate(processResult);
		timeStats.stop("Generate Sources");
	}

	private void handleException(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, ProcessingException e) {
		String errorMessage = errorHelper.getErrorMessage(processingEnv, e, getAAProcessorVersion());

		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.ERROR, errorMessage);

		/*
		 * Printing exception as an error on a random element. The exception is
		 * not related to this element, but otherwise it wouldn't show up in
		 * eclipse.
		 */

		Iterator<? extends TypeElement> iterator = annotations.iterator();
		if (iterator.hasNext()) {
			Element element = roundEnv.getElementsAnnotatedWith(iterator.next()).iterator().next();
			messager.printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
		} else {
			// Sometime this is a total mess and javac could not even find one
			// element on which we could print the error. So we should just
			// throw an exception and let it go.
			throw new RuntimeException("An error occured and couldn't be printed on an element: " + errorMessage);
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
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
					EIntentService.class, //
					ServiceAction.class, //
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
					HierarchyViewerSupport.class, //
					CheckedChange.class, //
					FocusChange.class //
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
	}
}
