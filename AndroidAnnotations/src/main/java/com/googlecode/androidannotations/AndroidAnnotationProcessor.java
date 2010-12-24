package com.googlecode.androidannotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.googlecode.androidannotations.generation.ModelGenerator;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.processing.ModelProcessor;
import com.googlecode.androidannotations.validation.ElementValidator;
import com.googlecode.androidannotations.validation.LayoutValidator;
import com.googlecode.androidannotations.validation.ModelValidator;
import com.googlecode.androidannotations.validation.ViewValidator;

@SupportedAnnotationTypes({ "com.googlecode.androidannotations.Layout", "com.googlecode.androidannotations.View" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AndroidAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		try {
			processThrowing(annotations, roundEnv);
		} catch (CompilationFailedException e) {
			// Does nothing, this exception is there to stop the compilation
			// flow.
			// Should be removed when code is refactored (no more compilation
			// flow interruption)
		} catch (Exception e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unexpected annotation processing exception: " + e.toString());
			e.printStackTrace();

			Element element = roundEnv.getElementsAnnotatedWith(annotations.iterator().next()).iterator().next();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"Unexpected annotation processing exception (not related to this element, but otherwise it wouldn't show up in eclipse) : " + e.toString(),
					element);
		}

		return false;
	}

	private void processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws IOException {
		
		AnnotationElementsHolder extractedModel = extract(annotations, roundEnv);

		RClass rClass = findRClass(extractedModel);
		
		AnnotationElements validatedModel = validate(extractedModel, rClass);

		MetaModel model = process(validatedModel);

		generate(model);
	}

	private AnnotationElementsHolder extract(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		ModelExtractor modelExtractor = new ModelExtractor();
		AnnotationElementsHolder extractedModel = modelExtractor.extract(annotations, roundEnv);
		return extractedModel;
	}
	
	private RClass findRClass(AnnotationElementsHolder extractedModel) {
		RClassFinder rClassFinder = new RClassFinder(processingEnv);
		RClass rClass = rClassFinder.find(extractedModel);
		return rClass;
	}


	private AnnotationElements validate(AnnotationElementsHolder extractedModel, RClass rClass) {
		if (rClass != null) {
			ModelValidator modelValidator = buildModelValidator(rClass);
			return modelValidator.validate(extractedModel);
		} else {
			return EmptyAnnotationElements.INSTANCE;
		}
	}

	private ModelValidator buildModelValidator(RClass rClass) {
		ElementValidator layoutValidator = new LayoutValidator(processingEnv, rClass);
		ElementValidator viewValidator = new ViewValidator(processingEnv, rClass);

		ModelValidator modelValidator = new ModelValidator();
		modelValidator.register(layoutValidator);
		modelValidator.register(viewValidator);
		return modelValidator;
	}

	private void generate(MetaModel model) throws IOException {
		ModelGenerator modelGenerator = new ModelGenerator(processingEnv.getFiler());
		modelGenerator.generate(model);
	}

	private MetaModel process(AnnotationElements validatedModel) {
		ModelProcessor modelProcessor = new ModelProcessor();
		return modelProcessor.process(validatedModel);
	}

}