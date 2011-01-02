package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.googlecode.androidannotations.annotations.Layout;
import com.googlecode.androidannotations.helper.HasTargetAnnotationHelper;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class LayoutProcessor extends HasTargetAnnotationHelper implements ElementProcessor {
	
	private final RClass rClass;

	public LayoutProcessor(ProcessingEnvironment processingEnv, RClass rClass) {
		super(processingEnv);
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Layout.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {
		
		TypeElement typeElement = (TypeElement) element;
		
		Layout layoutAnnotation = element.getAnnotation(Layout.class);
		int layoutIdValue = layoutAnnotation.value();
		
		RInnerClass rInnerClass = rClass.get(Res.LAYOUT);
		
		String layoutFieldQualifiedName = rInnerClass.getIdQualifiedName(layoutIdValue);
		
		String superClassQualifiedName = typeElement.getQualifiedName().toString();

		int packageSeparatorIndex = superClassQualifiedName.lastIndexOf('.');

		String packageName = superClassQualifiedName.substring(0, packageSeparatorIndex);

		String superClassSimpleName = superClassQualifiedName.substring(packageSeparatorIndex + 1);

		MetaActivity activity = new MetaActivity(packageName, superClassSimpleName, layoutFieldQualifiedName);
		
		metaModel.getMetaActivities().put(element, activity);
		
	}

}
