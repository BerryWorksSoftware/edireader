package com.berryworks.edireader.util;

import static com.berryworks.edireader.util.FixedLength.isPresent;
import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

public abstract class NameConverter {

    private static final char HYPHEN = '-';
    private static final char DOT = '.';
    private static final char UNDERSCORE = '_';
    public static final String LEADING_ZERO = "0";

    /**
     * Determines if a name has the form of an EDI segment name for any of the common EDI standards.
     * For example, "REF", "N3", "G62", "ZZZ", and even "ABCD" all have the form of a segment name
     * while "ref", "3N", and "ABCDE" do not.
     *
     * @param name String value to be considered
     * @return true only if the name has the form of a segment name
     */
    public static boolean isSegment(String name) {
        if (!isPresent(name)) return false;
        int length = name.length();
        if (length < 2 || length > 4) return false;
        if (!isUpperCase(name.charAt(0))) return false;
        for (int i = 1; i < length; i++) {
            char c = name.charAt(i);
            if (!(isUpperCase(c) || isDigit(c))) return false;
        }
        return true;
    }

    public static String withHyphen(String name) {
        return withHyphen(name, true);
    }

    public static String withHyphen(String name, boolean keep0AfterHyphen) {
        if (name.indexOf(HYPHEN) < 0) {
            int lengthMinus2 = name.length() - 2;
            String lastPart = name.substring(lengthMinus2);
            String firstPart = name.substring(0, lengthMinus2);
            if (!keep0AfterHyphen && lastPart.startsWith(LEADING_ZERO))
                lastPart = lastPart.substring(1);
            name = firstPart + HYPHEN + lastPart;
        }
        return name;
    }

    // beforeFirst ...
    public static String beforeFirstHyphen(String value) {
        return beforeFirstSeparator(value, HYPHEN);
    }

    public static String beforeFirstDot(String value) {
        return beforeFirstSeparator(value, DOT);
    }
    public static String beforeFirstUnderscore(String value) {
        return beforeFirstSeparator(value, UNDERSCORE);
    }
    public static String beforeFirstSeparator(String value, char separator) {
        int i = value.indexOf(separator);
        if (i < 0) return value;
        return value.substring(0, i).trim();
    }

    // afterFirst ...
    public static String afterFirstHyphen(String value) {
        return afterFirstSeparator(value, HYPHEN);
    }
    public static String afterFirstDot(String value) {
        return afterFirstSeparator(value, DOT);
    }
    public static String afterFirstUnderscore(String value) {
        return afterFirstSeparator(value, UNDERSCORE);
    }
    public static String afterFirstSeparator(String value, char separator) {
        int i = value.indexOf(separator);
        if (i < 0) return value;
        return value.substring(i + 1).trim();
    }

    // beforeLast ...
    public static String beforeLastHyphen(String value) {
        return beforeLastSeparator(value, HYPHEN);
    }
    public static String beforeLastDot(String value) {
        return beforeLastSeparator(value, DOT);
    }
    public static String beforeLastUnderscore(String value) {
        return beforeLastSeparator(value, UNDERSCORE);
    }
    public static String beforeLastSeparator(String value, char separator) {
        int i = value.lastIndexOf(separator);
        if (i < 0) return value;
        return value.substring(0, i).trim();
    }

    // afterLast ...
    public static String afterLastHyphen(String value) {
        return afterLastSeparator(value, HYPHEN);
    }
    public static String afterLastDot(String value) {
        return afterLastSeparator(value, DOT);
    }
    public static String afterLastUnderscore(String value) {
        return afterLastSeparator(value, UNDERSCORE);
    }
    public static String afterLastSeparator(String value, char separator) {
        int i = value.lastIndexOf(separator);
        if (i < 0) return value;
        return value.substring(i + 1).trim();
    }

    public static int valueAfterLastHyphen(String value) {
        final int i = value.lastIndexOf(HYPHEN);
        if (i < 0) return 0;

        int result = 0;
        try {
            result = Integer.parseInt(value.substring(i + 1));
        } catch (NumberFormatException ignore) {
        }
        return result;
    }

    public static int valueAfterFirstHyphen(String value) {
        int indexOfLastHyphen = value.lastIndexOf(HYPHEN);
        if (indexOfLastHyphen == value.indexOf(HYPHEN)) {
            // Must be 1 (or no) hyphen. Assume 1 for our purposes.
            return valueAfterLastHyphen(value);
        } else {
            // Must be 2 (or more) hyphens. Assume 2 for our purposes.
            value = value.substring(0, indexOfLastHyphen);
            return valueAfterLastHyphen(value);
        }
    }

    public static String withoutHyphenAndWithLeadingZero(String value) {
        String result = beforeFirstHyphen(value);
        if (result.length() < value.length()) {
            String suffix = afterFirstHyphen(value);
            if (suffix.length() == 1) {
                suffix = '0' + suffix;
            }
            result += suffix;
        }
        return result;
    }

    public static boolean hasHyphen(String value) {
        return value != null && value.indexOf(HYPHEN) >= 0;
    }

    public static boolean hasDot(String value) {
        return value != null && value.indexOf(DOT) >= 0;
    }

    public static String joinWithHyphen(String firstPart, int secondPart) {
        return firstPart + HYPHEN + secondPart;
    }

    public static String joinWithHyphen(String firstPart, String secondPart) {
        return firstPart + HYPHEN + secondPart;
    }

    public static boolean hasMultipleHyphens(String elementName) {
        int firstOne = elementName.indexOf(HYPHEN);
        return firstOne > 0 && elementName.substring(firstOne + 1).indexOf(HYPHEN) > 0;
    }

    public static String trim2TrailingDigits(String name) {
        if (name.length() > 2) {
            String candidateResult = name.substring(0, name.length() - 2);
            String trailer = name.substring(name.length() - 2);
            try {
                Integer.parseInt(trailer);
            } catch (NumberFormatException e) {
                return name;
            }
            return candidateResult;
        }
        return name;
    }

    public static int valueOf2TrailingDigits(String name) {
        if (name.length() > 2) {
            String trailer = name.substring(name.length() - 2);
            try {
                return Integer.parseInt(trailer);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static String expandCamelCase(String text) {
        if (text == null || text.isEmpty())
            return text;

        boolean armed = false;
        StringBuilder result = new StringBuilder();
        for (int n = 0; n < text.length(); n++) {
            char c = text.charAt(n);
            if (armed && Character.isUpperCase(c)) {
                result.append(' ');
            }
            result.append(c);
            if (c == ' ' || c == '-') {
                armed = false;
            } else if (Character.isUpperCase(c)) {
                if (text.length() <= n + 2) {
                    armed = false;
                } else {
                    char plus1 = text.charAt(n + 1);
                    char plus2 = text.charAt(n + 2);
                    armed = Character.isUpperCase(plus1) && Character.isLowerCase(plus2);
                }
            } else {
                armed = true;
            }
        }
        return result.toString();
    }

    public static String formXsdCompatibleName(String text) {
        if (text == null) return "";

        text = text.replace(',', ' ');
        text = text.replace('(', ' ');
        text = text.replace(')', ' ');
        text = text.replace('|', '-');
        text = text.replaceAll("'", "");

        StringBuilder result = new StringBuilder();
        for (String t : text.trim().split("[ /]+")) {
            if (t.isEmpty()) continue;
            char c = t.charAt(0);
            if (Character.isLowerCase(c)) {
                c = Character.toUpperCase(c);
                String remainder = "";
                if (t.length() > 1) {
                    remainder = t.substring(1);
                }
                t = c + remainder;
            }
            result.append(t);
        }
        return result.toString();
    }
}
