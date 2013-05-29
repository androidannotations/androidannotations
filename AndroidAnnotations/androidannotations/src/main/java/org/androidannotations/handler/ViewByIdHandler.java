package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.HasViewChanged;
import org.androidannotations.holder.ViewChangedHolder;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

public class ViewByIdHandler extends BaseAnnotationHandler<HasViewChanged> {

	private IdAnnotationHelper annotationHelper;

	public ViewByIdHandler(ProcessingEnvironment processingEnvironment) {
		super(ViewById.class, processingEnvironment);
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

		validatorHelper.isDeclaredType(element, valid);

		validatorHelper.extendsView(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.ID, IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, HasViewChanged holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror uiFieldTypeMirror = element.asType();
		String typeQualifiedName = uiFieldTypeMirror.toString();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, IRClass.Res.ID, true);

		ViewChangedHolder onViewChanged = holder.getOnViewChangedHolder();
		JBlock methodBody = onViewChanged.body();
		methodBody.assign(ref(fieldName), cast(holder.refClass(typeQualifiedName), onViewChanged.findViewById(idRef)));
	}
}
