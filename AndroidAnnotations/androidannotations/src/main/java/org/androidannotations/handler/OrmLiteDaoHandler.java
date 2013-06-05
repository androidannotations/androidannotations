package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.helper.TargetAnnotationHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.sun.codemodel.JExpr.ref;

public class OrmLiteDaoHandler extends BaseAnnotationHandler<EComponentHolder> {

	private TargetAnnotationHelper annotationHelper;

	public OrmLiteDaoHandler(ProcessingEnvironment processingEnvironment) {
		super(OrmLiteDao.class, processingEnvironment);
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.hasOrmLiteJars(element, valid);

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.extendsOrmLiteDaoWithValidModelParameter(element, valid);

		validatorHelper.hasASqlLiteOpenHelperParameterizedType(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) {
		String fieldName = element.getSimpleName().toString();

		TypeMirror modelObjectTypeMirror = annotationHelper.extractAnnotationParameter(element, "model");
		JExpression modelClass = holder.refClass(modelObjectTypeMirror.toString()).dotclass();

		TypeMirror databaseHelperTypeMirror = annotationHelper.extractAnnotationParameter(element, "helper");
		JFieldVar databaseHelperRef = holder.getDatabaseHelperRef(databaseHelperTypeMirror);

		JBlock initBody = holder.getInit().body();

		JTryBlock tryBlock = initBody._try();
		tryBlock.body().assign(ref(fieldName), databaseHelperRef.invoke("getDao").arg(modelClass));

		JCatchBlock catchBlock = tryBlock._catch(holder.classes().SQL_EXCEPTION);
		JVar exception = catchBlock.param("e");

		catchBlock.body() //
				.staticInvoke(holder.classes().LOG, "e") //
				.arg(holder.getGeneratedClass().name()) //
				.arg("Could not create DAO " + fieldName) //
				.arg(exception);
	}
}
