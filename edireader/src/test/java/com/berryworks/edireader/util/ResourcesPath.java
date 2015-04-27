package com.berryworks.edireader.util;

import java.io.File;

public class ResourcesPath {

    public static String locate(String path) {
        if (new File(path).exists())
            return path;

        String alternatePath = stripPrefix(path);
        if (new File(alternatePath).exists()) {
            path = alternatePath;
        } else {
            System.out.println("Cannot locate " + path + " or " + alternatePath);
        }

        return path;

    }

    public static String locate(String possibleParentDirectory, String directory) {
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

    private static String stripPrefix(String path) {
        int i = path.indexOf('/');
        return i >= 0 ? path.substring(i + 1) : path;
    }
}
