package org.androidannotations.handler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import org.androidannotations.annotations.res.HtmlRes;
import org.androidannotations.helper.AndroidManifest;
import org.androidannotations.helper.CanonicalNameConstants;
import org.androidannotations.helper.IdAnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AndroidRes;
import org.androidannotations.model.AndroidSystemServices;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.html.HTML;

import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;

public class ResHandler extends BaseAnnotationHandler<EComponentHolder> {

	private AndroidRes androidRes;
	private IdAnnotationHelper annotationHelper;

	public ResHandler(AndroidRes androidRes, ProcessingEnvironment processingEnvironment) {
		super(androidRes.getAnnotationClass(), processingEnvironment);
		this.androidRes = androidRes;
	}

	@Override
	public void setAndroidEnvironment(IRClass rClass, AndroidSystemServices androidSystemServices, AndroidManifest androidManifest) {
		super.setAndroidEnvironment(rClass, androidSystemServices, androidManifest);
		annotationHelper = new IdAnnotationHelper(processingEnv, getTarget(), rClass);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		TypeMirror fieldTypeMirror = element.asType();

		validatorHelper.allowedType(element, valid, fieldTypeMirror, androidRes.getAllowedTypes());

		validatorHelper.resIdsExist(element, androidRes.getRInnerClass(), IdValidatorHelper.FallbackStrategy.USE_ELEMENT_NAME, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		ProcessHolder.Classes classes = holder.classes();

		String fieldName = element.getSimpleName().toString();

		IRClass.Res resInnerClass = androidRes.getRInnerClass();

		JFieldRef idRef = annotationHelper.extractOneAnnotationFieldRef(holder, element, resInnerClass, true);

		JBlock methodBody = holder.getInit().body();

		TypeMirror fieldTypeMirror = element.asType();
		String fieldType = fieldTypeMirror.toString();

		// Special case for loading animations
		if (CanonicalNameConstants.ANIMATION.equals(fieldType)) {
			methodBody.assign(ref(fieldName), classes.ANIMATION_UTILS.staticInvoke("loadAnimation").arg(holder.getContextRef()).arg(idRef));
		} else {
			String resourceMethodName = androidRes.getResourceMethodName();

			// Special case for @HtmlRes
			if (element.getAnnotation(HtmlRes.class) != null) {
				methodBody.assign(ref(fieldName), classes.HTML.staticInvoke("fromHtml").arg(invoke(holder.getResourcesRef(), resourceMethodName).arg(idRef)));
			} else {
				methodBody.assign(ref(fieldName), invoke(holder.getResourcesRef(), resourceMethodName).arg(idRef));
			}
		}
	}
}
