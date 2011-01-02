/*
 * Copyright 2010-2011 Pierre-Yves Ricau (py.ricau at gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

		String generatedClass = String.format(CLASS_FORMAT, activity.getPackageName(), activity.getClassSimpleName(),
				activity.getSuperClassName(), activity.getLayoutQualifiedName(), metaViewBuilder.toString());

		writer.append(generatedClass);

	}

}
