package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

public class PrefHandler extends BaseAnnotationHandler<EComponentHolder> {

	public PrefHandler(ProcessingEnvironment processingEnvironment) {
		super(Pref.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.isSharedPreference(element, validatedElements, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {

		String fieldName = element.getSimpleName().toString();

		TypeMirror fieldTypeMirror = element.asType();

		String fieldType = fieldTypeMirror.toString();
		if (fieldTypeMirror instanceof ErrorType || fieldTypeMirror.getKind() == TypeKind.ERROR) {
			String elementTypeName = fieldTypeMirror.toString();
			String prefTypeName = elementTypeName.substring(0, elementTypeName.length() - GENERATION_SUFFIX.length());
			Set<? extends Element> sharedPrefElements = validatedModel.getRootAnnotatedElements(SharedPref.class.getName());

			for (Element sharedPrefElement : sharedPrefElements) {
				TypeElement sharedPrefTypeElement = (TypeElement) sharedPrefElement;

				String sharedPrefSimpleName = sharedPrefTypeElement.getSimpleName().toString();
				String sharedPrefQualifiedName = sharedPrefTypeElement.getQualifiedName().toString();

				if (sharedPrefSimpleName.equals(prefTypeName)) {
					fieldType = sharedPrefQualifiedName + GENERATION_SUFFIX;
					break;
				}
			}

		}

		JBlock methodBody = holder.getInit().body();

		JFieldRef field = JExpr.ref(fieldName);

		methodBody.assign(field, JExpr._new(holder.refClass(fieldType)).arg(holder.getContextRef()));

	}
}
