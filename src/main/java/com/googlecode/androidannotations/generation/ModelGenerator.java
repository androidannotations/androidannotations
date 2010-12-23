package com.googlecode.androidannotations.generation;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;

import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;

public class ModelGenerator {

	private final Filer filer;
	private final ActivityGenerator activityGenerator;

	public ModelGenerator(Filer filer) {
		this.filer = filer;
		activityGenerator = new ActivityGenerator();
	}

	public void generate(MetaModel model) throws IOException {
		for (MetaActivity activity : model.getMetaActivities().values()) {
			String sourceFileName = activity.getClassQualifiedName();
			FileObject sourceFile = filer.createSourceFile(sourceFileName);
			Writer writer = sourceFile.openWriter();
			try {
				activityGenerator.generate(activity, writer);
			} finally {
				writer.close();
			}
		}
	}

}
