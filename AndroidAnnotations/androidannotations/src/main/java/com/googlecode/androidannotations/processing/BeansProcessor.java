package com.googlecode.androidannotations.processing;

import static com.googlecode.androidannotations.helper.ModelConstants.GENERATION_SUFFIX;
import static com.googlecode.androidannotations.processing.EBeanProcessor.GET_INSTANCE_METHOD_NAME;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.Beans;
import com.googlecode.androidannotations.api.SetContentViewAware;
import com.googlecode.androidannotations.helper.GenericTypeFinder;
import com.googlecode.androidannotations.helper.TargetAnnotationHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;

public class BeansProcessor implements ElementProcessor {

	private TargetAnnotationHelper annotationHelper;

	public BeansProcessor(ProcessingEnvironment processingEnv) {
		annotationHelper = new TargetAnnotationHelper(processingEnv, getTarget());
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Beans.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) throws Exception {
		defaultCollection(element, codeModel, eBeansHolder);
		processCollectionItems(element, codeModel, eBeansHolder);
	}

	@SuppressWarnings("unchecked")
	private void processCollectionItems(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		JBlock init = holder.init.body();
		JBlock afterSetContentView = holder.afterSetContentView.body();
		String fieldName = element.getSimpleName().toString();

		if (holder.afterSetContentView != null) {
			afterSetContentView = holder.afterSetContentView.body();
			JForEach forEach = afterSetContentView.forEach(codeModel.ref(Object.class), "item", ref(fieldName));
			forEach.body().invoke(cast(codeModel.ref(SetContentViewAware.class), ref("item")), SetContentViewAware.SIGNATURE);
		}

		List<? extends AnnotationValue> eBeans = (List<? extends AnnotationValue>) annotationHelper.extractAnnotationClassAttrVal(element, "value");
		if (eBeans != null) {
			for (AnnotationValue bean : eBeans) {
				JClass injectedClass = codeModel.ref(bean.getValue().toString() + GENERATION_SUFFIX);
				JInvocation getInstance = injectedClass.staticInvoke(GET_INSTANCE_METHOD_NAME).arg(holder.contextRef);
				init.invoke(ref(fieldName), "add").arg(getInstance);
			}
		}
	}

	private void defaultCollection(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);
		String fieldName = element.getSimpleName().toString();

		TypeMirror elementDeclaredType = element.asType();
		TypeMirror typed = GenericTypeFinder.newInstance(elementDeclaredType, annotationHelper).get(0);
		if (annotationHelper.isAssignableFromAny(elementDeclaredType, Collection.class, List.class)) {
			assignNarrowedCollection(codeModel, holder.init.body(), ArrayList.class.getCanonicalName(), fieldName, typed);
			return;
		}
		if (annotationHelper.isAssignable(Set.class, elementDeclaredType)) {
			assignNarrowedCollection(codeModel, holder.init.body(), HashSet.class.getCanonicalName(), fieldName, typed);
			return;
		}
	}

	private void assignNarrowedCollection(JCodeModel codeModel, JBlock body, String providedImpl, String fieldName, TypeMirror narrowed) {
		JClass injectedClass = codeModel.ref(providedImpl).narrow(codeModel.ref(narrowed.toString()));
		assignCollection(codeModel, body, injectedClass, fieldName);
	}

	private void assignCollection(JCodeModel codeModel, JBlock body, JClass injectedClass, String fieldName) {
		JInvocation getInstance = JExpr._new(injectedClass);
		JFieldRef ref = ref(fieldName);
		body._if(ref.eq(JExpr._null()))._then().assign(ref, getInstance);
	}
}
