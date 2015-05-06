package com.berryworks.edireader.util;

import java.io.File;

public abstract class VerboseTestCase {
    protected boolean verbose;

    // TODO These should probably be in a static TestDirectories class
    private String testdataPath, testresultsPath;

    public VerboseTestCase() {
        checkVerbose();
    }

    private void checkVerbose() {
        if (System.getProperty("verbose") != null) {
            verbose = true;
            System.out.println("verbose test output");
        }
    }

    protected void trace(String msg) {
        System.out.println(msg);
    }

    public String getTestdataPath() {
        if (testdataPath == null) {
            testdataPath = "testdata";
            File directory = new File(testdataPath);
            if (!directory.exists() || !directory.isDirectory()) {
                testdataPath = "../testdata";
            }
        }
        return testdataPath + "/";
    }

    public String getTestresultsPath() {
        if (testresultsPath == null) {
            testresultsPath = "testresults";
            File directory = new File(testresultsPath);
            if (!directory.exists() || !directory.isDirectory()) {
                testresultsPath = "../testresults";
            }
        }
        return testresultsPath + "/";
    }
}
