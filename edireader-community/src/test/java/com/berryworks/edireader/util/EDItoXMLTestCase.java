package com.berryworks.edireader.util;

import org.custommonkey.xmlunit.XMLTestCase;

public class EDItoXMLTestCase extends XMLTestCase {

    protected String testdataPath, testresultsPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MyTestCase testCase = (new MyTestCase());
        testdataPath = testCase.getTestdataPath();
        testresultsPath = testCase.getTestresultsPath();
    }

    public void testNothing() {
    }

    private class MyTestCase extends VerboseTestCase {

    }
}
