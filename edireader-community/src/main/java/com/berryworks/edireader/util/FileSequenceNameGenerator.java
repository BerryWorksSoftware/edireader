package com.berryworks.edireader.util;

public class FileSequenceNameGenerator {
    protected final String filenamePattern;
    protected String filenameSuffix = "", filenamePrefix = "";
    protected int sequenceNumberLength;

    private int count;

    public FileSequenceNameGenerator(String filenamePattern) {
        this.filenamePattern = filenamePattern;
        establishPattern();
    }

    private void establishPattern() {
        boolean haveSeen0 = false;
        for (int n = filenamePattern.length() - 1; n >= 0; n--) {
            char c = filenamePattern.charAt(n);
            if (c == '0') {
                haveSeen0 = true;
                sequenceNumberLength++;
            } else if (haveSeen0) {
                filenamePrefix = filenamePattern.substring(0, n + 1);
                break;
            } else {
                filenameSuffix = c + filenameSuffix;
            }
        }

        if (!haveSeen0)
            throw new RuntimeException("Filename pattern must include a sequence one or more '0's.");
    }

    public String generateName() {
        String sequenceDigits = "" + (100000 + ++count);
        sequenceDigits = sequenceDigits.substring(sequenceDigits.length() - sequenceNumberLength);
        return filenamePrefix + sequenceDigits + filenameSuffix;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }
}
