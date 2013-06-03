package org.androidannotations.handler;

import com.sun.codemodel.JClass;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.helper.ModelConstants;
import org.androidannotations.holder.EApplicationHolder;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import static com.sun.codemodel.JExpr.ref;

public class AppHandler extends BaseAnnotationHandler<EComponentHolder> {

	public AppHandler(ProcessingEnvironment processingEnvironment) {
		super(App.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.typeHasAnnotation(EApplication.class, element, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();
		String applicationQualifiedName = element.asType().toString();
		JClass applicationClass = holder.refClass(applicationQualifiedName + ModelConstants.GENERATION_SUFFIX);

		holder.getInit().body().assign(ref(fieldName), applicationClass.staticInvoke(EApplicationHolder.GET_APPLICATION_INSTANCE));
	}
}
