/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.generation;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class ResourceCodeWriter extends CodeWriter {

	private final Filer filer;

	public ResourceCodeWriter(Filer filer) {
		this.filer = filer;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		FileObject resource = filer.createResource(StandardLocation.SOURCE_OUTPUT, pkg.name(), fileName);
		return resource.openOutputStream();
	}

	@Override
	public void close() throws IOException {
	}
}
