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
package com.googlecode.androidannotations.experimental;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.androidannotations.rclass.IRClass;
import com.googlecode.androidannotations.rclass.IRInnerClass;
import com.googlecode.androidannotations.rclass.RInnerClass;
import com.googlecode.androidannotations.rclass.IRClass.Res;

public class FileSystemRClass implements IRClass {
	
	private final Map<String, RInnerClass> rClass = new HashMap<String, RInnerClass>();

	public FileSystemRClass(File resFolder, String rClassPackage) throws IOException {
        File layoutFolder = new File(resFolder, "layout");

        File[] layoutFiles = layoutFolder.listFiles();
        File layout = layoutFiles[0];
        FileReader reader = new FileReader(layout);

        BufferedReader br = new BufferedReader(reader);

        String line;
        String id = "nothing";
        while ((line = br.readLine()) != null) {
            int idIndex = line.indexOf("android:id=\"@+id");
            if (idIndex != -1) {
                int endOfId = line.lastIndexOf("\"");
                id = line.substring(idIndex + "android:id=\"@+id".length() + 1, endOfId);
                break;
            }
        }
	}

	@Override
	public IRInnerClass get(Res res) {

		String id = res.rName();

		IRInnerClass rInnerClass = rClass.get(id);
		if (rInnerClass != null) {
			return rInnerClass;
		} else {
			return RInnerClass.EMPTY_R_INNER_CLASS;
		}
	}
}
