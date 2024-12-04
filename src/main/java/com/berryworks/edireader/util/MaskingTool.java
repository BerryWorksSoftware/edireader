package com.berryworks.edireader.util;

/**
 * A device used in testing to help compare an expected result with an actual result, disregarding specific
 * character sequences marked in the mask by a span of 4 or more '?'.
 * <p>
 * Date/time generated fields are an example of where this comes in handy. This class is placed here so that it can
 * be easily accessed from a number of test environments. Since it is small and stand-alone, it is harmless to include
 * even though not expected to ever be used in a production scenario.
 */
public abstract class MaskingTool {

    public static String mask(String inputData, String mask) {
        StringBuilder sb = new StringBuilder();

        // A bit of cleansing first. If the input data has /r/n line separators, while the mask does not,
        // then replace each \r\n separator with \n.
        if (inputData.contains("\r\n") && !mask.contains("\r\n")) {
            inputData = inputData.replace("\r\n", "\n");
        }

        // Iterate through the mask, looking for instances of "????..."
        int lengthOfOriginal = inputData.length();
        int index = 0;
        while (true) {
            int indexOfMaskPattern = mask.indexOf("????", index);
            if (indexOfMaskPattern < 0) {
                if (index <= lengthOfOriginal) {
                    String trailer = inputData.substring(index);
                    sb.append(trailer);
                }
                break;
            }

            String firstPart;
            if (indexOfMaskPattern > lengthOfOriginal) {
                if (index < inputData.length()) {
                    firstPart = inputData.substring(index);
                    sb.append(firstPart);
                }
                break;
            } else {
                firstPart = inputData.substring(index, indexOfMaskPattern);
                sb.append(firstPart);
            }
            int lengthOfPattern = 0;
            for (int i = indexOfMaskPattern; i < mask.length(); i++) {
                char c = mask.charAt(i);
                if (c != '?') {
                    break;
                }
                sb.append(c);
                lengthOfPattern++;
            }
            index = indexOfMaskPattern + lengthOfPattern;
        }

        return sb.toString();
    }

}
