package com.berryworks.edireader.util;

import java.io.File;

public abstract class PathFinder {

    public static String confirmDirectory(String possibleParentDirectory, String directory) {
        String result = directory;
        File resourcesDirectory = new File(result);
        if (resourcesDirectory.exists() && resourcesDirectory.isDirectory()) {
            // confirmed!
        } else {
            result = possibleParentDirectory + '/' + directory;
            resourcesDirectory = new File(result);
            if (resourcesDirectory.exists() && resourcesDirectory.isDirectory()) {
            } else {
                throw new RuntimeException("Unable to confirm " + directory);
            }
        }
        return result;
    }

}
