package com.berryworks.edireader.util;

public class EDItoXMLTestCase {

    protected String testdataPath, testresultsPath;

    public void setUp() throws Exception {
        MyTestCase testCase = (new MyTestCase());
        testdataPath = testCase.getTestdataPath();
        testresultsPath = testCase.getTestresultsPath();
    }

    public void testNothing() {
    }

    private static class MyTestCase extends VerboseTestCase {

    }
}
