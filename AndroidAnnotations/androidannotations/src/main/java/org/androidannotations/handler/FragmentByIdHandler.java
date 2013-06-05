package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentWithViewSupportHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.*;

public class FragmentByIdHandler extends BaseAnnotationHandler<EComponentWithViewSupportHolder> {

	private IdAnnotationHelper annotationHelper;

	public FragmentByIdHandler(ProcessingEnvironment processingEnvironment) {
		super(FragmentById.class, processingEnvironment);
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedViewSupportAnnotation(element, validatedElements, valid);

		validatorHelper.extendsFragment(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentWithViewSupportHolder holder) {

		TypeMirror elementType = element.asType();
		String typeQualifiedName = elementType.toString();
		TypeElement nativeFragmentElement = annotationHelper.typeElementFromQualifiedName(CanonicalNameConstants.FRAGMENT);
		boolean isNativeFragment = nativeFragmentElement != null && annotationHelper.isSubtype(elementType, nativeFragmentElement.asType());

		JMethod findFragmentById;
		if (isNativeFragment) {
			findFragmentById = holder.getFindNativeFragmentById();
		} else {
			findFragmentById = holder.getFindSupportFragmentById();
		}

		String fieldName = element.getSimpleName().toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.ID, true);

		JBlock methodBody = holder.getOnViewChangedBody();

		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), invoke(findFragmentById).arg(idRef)));

	}
}
