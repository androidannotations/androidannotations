package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.View;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.model.MetaView;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RClass.Res;
import com.googlecode.androidannotations.rclass.RInnerClass;

public class ViewProcessor implements ElementProcessor {

	private final RClass rClass;

	public ViewProcessor(RClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return View.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String name = element.getSimpleName().toString();

		TypeMirror uiFieldTypeMirror = element.asType();
		String typeQualifiedName = uiFieldTypeMirror.toString();

		View viewAnnotation = element.getAnnotation(View.class);
		int viewIdValue = viewAnnotation.value();

		RInnerClass rInnerClass = rClass.get(Res.ID);
		String viewQualifiedId;
		if (viewIdValue == View.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();

			viewQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
		} else {
			viewQualifiedId = rInnerClass.getIdQualifiedName(viewIdValue);
		}

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<MetaView> metaViews = metaActivity.getMetaViews();

		MetaView metaView = new MetaView(name, typeQualifiedName, viewQualifiedId);
		metaViews.add(metaView);

	}

}
