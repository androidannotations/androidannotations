package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.helper.APTCodeModelHelper;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.holder.NonConfigurationHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.*;
import static org.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;

public class NonConfigurationInstanceHandler extends BaseAnnotationHandler<EActivityHolder> {

	private final APTCodeModelHelper codeModelHelper;
	private final AnnotationHelper annotationHelper;

	public NonConfigurationInstanceHandler(ProcessingEnvironment processingEnvironment) {
		super(NonConfigurationInstance.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
		codeModelHelper = new APTCodeModelHelper();
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEActivity(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EActivityHolder holder) throws JClassAlreadyExistsException {
		String fieldName = element.getSimpleName().toString();
		JClass fieldType = codeModelHelper.typeMirrorToJClass(element.asType(), holder);

		NonConfigurationHolder ncHolder = holder.getNonConfigurationHolder();
		JFieldVar ncHolderField = ncHolder.createField(fieldName, fieldType);

		injectInInit(element, holder, fieldName, ncHolderField);
		retainInOnRetain(holder, fieldName, ncHolderField);
	}

	private void injectInInit(Element element, EActivityHolder holder, String fieldName, JFieldVar ncHolderField) throws JClassAlreadyExistsException {
		JBlock initIfNonConfigurationNotNullBlock = holder.getInitIfNonConfigurationNotNullBlock();
		JVar initNonConfigurationInstance = holder.getInitNonConfigurationInstance();
		initIfNonConfigurationNotNullBlock.assign(ref(fieldName), initNonConfigurationInstance.ref(ncHolderField));
		rebindContextIfBean(element, holder, initIfNonConfigurationNotNullBlock, ncHolderField);
	}

	private void retainInOnRetain(EActivityHolder holder, String fieldName, JFieldVar ncHolderField) throws JClassAlreadyExistsException {
		JBlock onRetainNonConfigurationInstanceBindBlock = holder.getOnRetainNonConfigurationInstanceBindBlock();
		JVar onRetainNonConfigurationInstance = holder.getOnRetainNonConfigurationInstance();
		onRetainNonConfigurationInstanceBindBlock.assign(onRetainNonConfigurationInstance.ref(ncHolderField), ref(fieldName));
	}

	private void rebindContextIfBean(Element element, EActivityHolder holder, JBlock initIfNonConfigurationNotNullBlock, JFieldVar field) {
		boolean hasBeanAnnotation = element.getAnnotation(Bean.class) != null;
		if (hasBeanAnnotation) {

			TypeMirror elementType = annotationHelper.extractAnnotationClassParameter(element, Bean.class.getName());
			if (elementType == null) {
				elementType = element.asType();
			}
			String typeQualifiedName = elementType.toString();
			JClass fieldGeneratedBeanClass = holder.refClass(typeQualifiedName + GENERATION_SUFFIX);

			initIfNonConfigurationNotNullBlock.invoke(cast(fieldGeneratedBeanClass, field), "rebind").arg(_this());
		}
	}
}
