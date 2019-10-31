package com.berryworks.edireader.util;

import java.io.File;

public class ResourceEquipped {

    private final String prefix;
    private File testdataPath, testresultsPath;

    protected ResourceEquipped() {
        prefix = ResourcesPath.locate("edireader", "src/test/resources/");
    }

    public String prefix(String baseName) {
        return prefix + baseName;
    }

    protected File locateResource(String filename) {
        final File resourceFile = new File(prefix(filename));
        if (!resourceFile.exists())
            throw new RuntimeException("Cannot locate resource file: " + filename);
        return resourceFile;
    }

    protected File locateTestData(String filename) {
        final String pathname = getTestdataPath() + "/" + filename;
        final File testdata = new File(pathname);
        if (!testdata.exists())
            throw new RuntimeException("Cannot locate test data: " + filename);
        return testdata;
    }

    public File getTestdataPath() {

        if (testdataPath == null) {
            testdataPath = new File("testdata");
            if (!testdataPath.exists() || !testdataPath.isDirectory()) {
                testdataPath = new File("../testdata");
            }
        }
        return testdataPath;
    }

    public File getTestresultsPath() {

        if (testresultsPath == null) {
            testresultsPath = new File("testresults");
            if (!testresultsPath.exists() || !testresultsPath.isDirectory()) {
                testresultsPath = new File("../testresults");
            }
        }
        return testresultsPath;
    }


}
