package com.googlecode.androidannotations.generation;

import com.googlecode.androidannotations.model.MetaView;

public class ViewGenerator {
	
	private static final String FIELD_FORMAT = "        %s = (%s) findViewById(%s);\n";

	public String generate(MetaView metaView) {
		return String.format(FIELD_FORMAT, metaView.getFieldName(), metaView.getTypeQualifiedName(), metaView.getViewQualifiedId());
	}

}
