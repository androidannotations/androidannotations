package com.googlecode.androidannotations;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;

public class GeneratedActivity {
	
	private static final String CLASS_FORMAT = //
	"package %s;\n" + //
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
	
	
	
	private String packageName;
	
	private String classSimpleName;
	
	private String superClassQualifiedName;
	
	private String layoutQualifiedName;
	
	private final List<GeneratedField> generatedFields = new ArrayList<GeneratedField>();
	
	public void generateSource(Filer filer) throws IOException {
		
		FileObject classFile = filer.createSourceFile(packageName+"."+classSimpleName);
		
		StringBuilder generatedFieldsSb = new StringBuilder();
		
		for(GeneratedField generatedField : generatedFields) {
			generatedFieldsSb.append(generatedField.writeField());
		}
		
 		String generatedClass = String.format(CLASS_FORMAT, packageName, classSimpleName, superClassQualifiedName, layoutQualifiedName, generatedFieldsSb.toString());
		
 		Writer writer = classFile.openWriter();
 		writer.append(generatedClass);
 		writer.close();
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setSuperClassQualifiedName(String superClassQualifiedName) {
		this.superClassQualifiedName = superClassQualifiedName;
	}

	public void setLayoutQualifiedName(String layoutQualifiedName) {
		this.layoutQualifiedName = layoutQualifiedName;
	}

	public List<GeneratedField> getGeneratedFields() {
		return generatedFields;
	}

	public void setClassSimpleName(String classSimpleName) {
		this.classSimpleName = classSimpleName;
	}
	
}
