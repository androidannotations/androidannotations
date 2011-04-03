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
package com.googlecode.androidannotations.rclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.googlecode.androidannotations.helper.AnnotationHelper;
import com.googlecode.androidannotations.model.AnnotationElements;

public class CompiledRClassFinder extends AnnotationHelper implements RClassFinder {

    public CompiledRClassFinder(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    public IRClass find(AnnotationElements extractedModel) throws IOException {

        File manifestFile = findManifestFile();

        String rClassPackage = extractPackage(manifestFile);

        if (rClassPackage == null) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(Kind.WARNING, "Could not find the AndroidManifest.xml file in " + manifestFile.getAbsolutePath());
            return IRClass.EMPTY_R_CLASS;
        }

        Elements elementUtils = processingEnv.getElementUtils();
        String rClass = rClassPackage + ".R";
        TypeElement rType = elementUtils.getTypeElement(rClass);

        if (rType == null) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(Kind.WARNING, "The AndroidManifest.xml file was found, but not the compiled R class: " + rClass);
            return IRClass.EMPTY_R_CLASS;
        }

        return new RClass(rType);
    }

    private String extractPackage(File manifestFile) throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(manifestFile);
        BufferedReader br = new BufferedReader(reader);

        String line;
        while ((line = br.readLine()) != null) {

            ManifestPackageExtractor extractor = new ManifestPackageExtractor(line);

            if (extractor.matches()) {
                return extractor.extract();
            }
        }
        return null;
    }

    private File findManifestFile() throws IOException {
        Filer filer = processingEnv.getFiler();

        FileObject res = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "");
        File projectRoot = new File(res.toUri()).getParentFile();

        File androidManifestFile = new File(projectRoot, "AndroidManifest.xml");
        return androidManifestFile;
    }

}
