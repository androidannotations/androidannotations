package com.googlecode.androidannotations.processing;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.generation.ItemLongClickInstruction;
import com.googlecode.androidannotations.model.Instruction;
import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;
import com.googlecode.androidannotations.rclass.RClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.RClass.Res;

public class ItemLongClickProcessor implements ElementProcessor {

	private final RClass rClass;

	public ItemLongClickProcessor(RClass rClass) {
		this.rClass = rClass;
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return ItemLongClick.class;
	}

	@Override
	public void process(Element element, MetaModel metaModel) {

		String methodName = element.getSimpleName().toString();

		ItemLongClick annotation = element.getAnnotation(ItemLongClick.class);
		int idValue = annotation.value();

		RInnerClass rInnerClass = rClass.get(Res.ID);
		String itemClickQualifiedId;

		if (idValue == ItemLongClick.DEFAULT_VALUE) {
			String fieldName = element.getSimpleName().toString();
			itemClickQualifiedId = rInnerClass.getIdQualifiedName(fieldName);
		} else {
			itemClickQualifiedId = rInnerClass.getIdQualifiedName(idValue);
		}

		Element enclosingElement = element.getEnclosingElement();
		MetaActivity metaActivity = metaModel.getMetaActivities().get(enclosingElement);
		List<Instruction> onCreateInstructions = metaActivity.getOnCreateInstructions();

		Instruction instruction = new ItemLongClickInstruction(methodName, itemClickQualifiedId);
		onCreateInstructions.add(instruction);

	}

}
