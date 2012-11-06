package com.googlecode.androidannotations.processing;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.androidannotations.AndroidAnnotationProcessor;
import com.googlecode.androidannotations.utils.AAProcessorTestHelper;

/**
 * Author: Eugen Martynov
 */
public class EActivityProcessorTest extends AAProcessorTestHelper {
    @Before
    public void setup() {
        addManifestProcessorParameter(EActivityProcessorTest.class);
        addProcessor(AndroidAnnotationProcessor.class);
    }

    @Test
    public void activity_subclass_compiles() {
        assertCompilationSuccessful(compileFiles(SomeActivity.class));
    }

    @Test
    public void activity_subclass_has_derived_constructors() throws ClassNotFoundException, MalformedURLException {
        assertCompilationSuccessful(compileFiles(SomeActivity.class));

        URLClassLoader loader = new URLClassLoader(new URL[] {getOuputDirectory().toURI().toURL()});

        Class derivedClass = loader.loadClass("com.googlecode.androidannotations.processing.SomeActivity_");
        Class originalClass = loader.loadClass("com.googlecode.androidannotations.processing.SomeActivity");

        ArrayList<Constructor> derivedConstructors = new ArrayList<Constructor>(Arrays.asList(derivedClass.getConstructors()));
        ArrayList<Constructor> originalConstructors = new ArrayList<Constructor>(Arrays.asList(originalClass.getConstructors()));

        assertEquals("Number of constructors is same", originalConstructors.size(), derivedConstructors.size());

        Collections.sort(derivedConstructors, new ConstructorComparator());
        Collections.sort(originalConstructors, new ConstructorComparator());

        for (int i = 0; i < originalConstructors.size(); i++) {
            Constructor originalConstructor = originalConstructors.get(i);
            Constructor derivedConstructor = derivedConstructors.get(i);
            checkConstructors(originalConstructor, derivedConstructor);
        }
    }

    private void checkConstructors(Constructor originalConstructor, Constructor derivedConstructor) {
        assertEquals("Modifiers should be same", originalConstructor.getModifiers(), derivedConstructor.getModifiers());

        final Class<?>[] originalParameterTypes = originalConstructor.getParameterTypes();
        final Class<?>[] derivedParameterTypes = derivedConstructor.getParameterTypes();
        assertEquals("Parameters count should be same", originalParameterTypes.length, derivedParameterTypes.length);

        for (int i = 0; i < originalParameterTypes.length; i++) {
            assertEquals("Parameter type should be same", originalParameterTypes[i], derivedParameterTypes[i]);
        }
    }

    private class ConstructorComparator implements Comparator<Constructor> {
        @Override
        public int compare(Constructor o1, Constructor o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }
}
