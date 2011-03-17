package com.googlecode.androidannotations.generation;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.tools.FileObject;

import com.googlecode.androidannotations.model.MetaActivity;
import com.googlecode.androidannotations.model.MetaModel;

public class StringModelGenerator {

        private final Filer filer;
        private final ActivityGenerator activityGenerator;

        public StringModelGenerator(Filer filer) {
                this.filer = filer;
                activityGenerator = new ActivityGenerator();
        }

        public void generate(MetaModel model) throws IOException {
                for (MetaActivity activity : model.getMetaActivities().values()) {
                        String sourceFileName = activity.getClassQualifiedName();
                        FileObject sourceFile;
                        try {
                                sourceFile = filer.createSourceFile(sourceFileName);
                        } catch (FilerException e) {
                                // TODO Is this a good idea ? This exception seems to happen
                                // when there is a compilation error not linked to the
                                // annotations but rather a Java compilation issue.
                                return;
                        }
                        Writer writer = sourceFile.openWriter();
                        try {
                                activityGenerator.generate(activity, writer);
                        } finally {
                                writer.close();
                        }
                }
        }

}

