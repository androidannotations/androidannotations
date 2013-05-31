package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

public class SystemServiceHandler extends BaseAnnotationHandler<EComponentHolder> {

	public SystemServiceHandler(ProcessingEnvironment processingEnvironment) {
		super(SystemService.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.androidService(androidSystemServices, element, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror serviceType = element.asType();
		String fieldTypeQualifiedName = serviceType.toString();

		JFieldRef serviceRef = androidSystemServices.getServiceConstant(serviceType, holder);

		JBlock methodBody = holder.getInit().body();

		methodBody.assign(ref(fieldName), cast(holder.refClass(fieldTypeQualifiedName), holder.getContextRef().invoke("getSystemService").arg(serviceRef)));
	}
}
