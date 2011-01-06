package com.googlecode.androidannotations.generation;

import com.googlecode.androidannotations.model.Instruction;

public class ItemClickInstruction implements Instruction {

	private static final String FORMAT = //
	"" + //
			"        ((android.widget.AdapterView<?>) findViewById(%s)).setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {\n" + //
			"			@Override\n" + //
			"			public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {\n" + //
			"				%s(parent, view, position, id);\n" + //
			"			}\n" + //
			"		});\n" + //
			"\n";

	private final String methodName;

	private final String clickQualifiedId;

	public ItemClickInstruction(String methodName, String clickQualifiedId) {
		this.methodName = methodName;
		this.clickQualifiedId = clickQualifiedId;
	}

	@Override
	public String generate() {
		return String.format(FORMAT, clickQualifiedId, methodName);
	}

}
