package com.googlecode.androidannotations.rclass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ManifestPackageExtractorTest{
    
    @Test
    public void simpleMatches() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("package=\"com.toto\"");
        assertTrue(extractor.matches());
    }
    
    @Test
    public void spacesMatches() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("    package    =    \"com.toto\"    ");
        assertTrue(extractor.matches());
    }

    @Test
    public void fooDoesNotMatch() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("Hello you");
        assertFalse(extractor.matches());
    }
    
    @Test
    public void emptyPackageDoesNotMatch() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("package=\"\"");
        assertFalse(extractor.matches());
    }
    
    @Test
    public void simpleExtract() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("package=\"com.toto\"");
        assertEquals("com.toto", extractor.extract());
    }
    
    @Test
    public void fooExtractsNull() {
        ManifestPackageExtractor extractor = new ManifestPackageExtractor("Hello you");
        assertNull(extractor.extract());
    }
    
    @Test
    public void inputIsNull() {
        new ManifestPackageExtractor(null).extract();
    }
    

}
