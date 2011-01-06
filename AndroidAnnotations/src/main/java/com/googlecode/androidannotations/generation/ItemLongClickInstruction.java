package com.googlecode.androidannotations.generation;

import com.googlecode.androidannotations.model.Instruction;

public class ItemLongClickInstruction implements Instruction {

	private static final String FORMAT = //
	"" + //
			"        ((android.widget.AdapterView<?>) findViewById(%s)).setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {\n" + //
			"			@Override\n" + //
			"			public boolean onItemLongClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {\n" + //
			"				return %s(parent, view, position, id);\n" + //
			"			}\n" + //
			"		});\n" + //
			"\n";

	private final String methodName;

	private final String clickQualifiedId;

	public ItemLongClickInstruction(String methodName, String clickQualifiedId) {
		this.methodName = methodName;
		this.clickQualifiedId = clickQualifiedId;
	}

	@Override
	public String generate() {
		return String.format(FORMAT, clickQualifiedId, methodName);
	}

}
