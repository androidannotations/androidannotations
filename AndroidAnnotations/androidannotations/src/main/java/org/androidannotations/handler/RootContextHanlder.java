package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.holder.EBeanHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

public class RootContextHanlder extends BaseAnnotationHandler<EBeanHolder> {

	public RootContextHanlder(ProcessingEnvironment processingEnvironment) {
		super(RootContext.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEBeanAnnotation(element, validatedElements, valid);

		validatorHelper.extendsContext(element, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EBeanHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();

		JBlock body = holder.getInit().body();
		JExpression contextRef = holder.getContextRef();

		if (CanonicalNameConstants.CONTEXT.equals(typeQualifiedName)) {
			body.assign(ref(fieldName), contextRef);
		} else {
			JClass extendingContextClass = holder.refClass(typeQualifiedName);
			body._if(contextRef._instanceof(extendingContextClass)) //
					._then() //
					.assign(ref(fieldName), cast(extendingContextClass, contextRef));
		}
	}
}
