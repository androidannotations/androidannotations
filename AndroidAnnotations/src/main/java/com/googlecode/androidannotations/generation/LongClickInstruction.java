package com.googlecode.androidannotations.generation;

import com.googlecode.androidannotations.model.Instruction;

public class LongClickInstruction implements Instruction {

	private static final String FORMAT = //
	"" + //
			"        (findViewById(%s)).setOnLongClickListener(new android.view.View.OnLongClickListener() {\n" + //
			"			public boolean onLongClick(android.view.View v) {\n" + //
			"				return %s(%s);\n" + //
			"			}\n" + //
			"		});\n" + //
			"\n";

	private final String methodName;

	private final String clickQualifiedId;

	private final boolean viewParameter;

	public LongClickInstruction(String methodName, String clickQualifiedId, boolean viewParameter) {
		this.methodName = methodName;
		this.clickQualifiedId = clickQualifiedId;
		this.viewParameter = viewParameter;
	}

	@Override
	public String generate() {
		String viewParameterValue = viewParameter ? "v" : "";
		return String.format(FORMAT, clickQualifiedId, methodName, viewParameterValue);
	}

}
