package com.googlecode.androidannotations.generation;

import java.io.IOException;
import java.io.Writer;

import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaView;

public class ActivityGenerator {

	private static final String CLASS_FORMAT = //
	"" + //
			"package %s;\n" + //
			"\n" + //
			"\n" + //
			"public class %s extends %s {\n" + //
			"    @Override\n" + //
			"    public void onCreate(android.os.Bundle savedInstanceState) {\n" + //
			"        setContentView(%s);\n" + //
			"\n" + //
			"%s" + //
			"\n" + //
			"        super.onCreate(savedInstanceState);\n" + //
			"    }\n" + //
			"}\n";

	private final ViewGenerator viewGenerator;

	public ActivityGenerator() {
		viewGenerator = new ViewGenerator();
	}

	public void generate(MetaActivity activity, Writer writer) throws IOException {
		StringBuilder metaViewBuilder = new StringBuilder();

		for (MetaView metaView : activity.getMetaViews()) {
			metaViewBuilder.append(viewGenerator.generate(metaView));
		}

		String generatedClass = String.format(CLASS_FORMAT, activity.getPackageName(), activity.getClassSimpleName(), activity.getSuperClassName(),
				activity.getLayoutQualifiedName(), metaViewBuilder.toString());

		writer.append(generatedClass);

	}

}
