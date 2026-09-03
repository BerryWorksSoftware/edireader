package com.berryworks.edireader.util;

import static org.junit.Assert.assertEquals;

public class TestUtil {
    public static void assertEqualsDisregardingLineSeparators(String expected, String actual) {
        if (expected.equals(actual)) return;
        String expectedAdjusted = expected.replace("\r", "").replace("\n", "");
        String actualAdjusted = actual.replace("\r", "").replace("\n", "");
        if (expectedAdjusted.equals(actualAdjusted)) return;
        assertEquals(expected, actual);
    }

    public static void assertStartsWithDisregardingLineSeparators(String expected, String actual) {
        if (expected.equals(actual)) return;
        String expectedAdjusted = expected.replace("\r", "").replace("\n", "");
        String actualAdjusted = actual.replace("\r", "").replace("\n", "");
        if (actualAdjusted.length() > expectedAdjusted.length()) {
            actualAdjusted = actualAdjusted.substring(0, expectedAdjusted.length());
        }
        if (expectedAdjusted.equals(actualAdjusted)) return;
        assertEquals(expectedAdjusted, actualAdjusted);
    }

    public static void assertEqualsDisregardingSpacesAndLineSeparators(String expected, String actual) {
        if (expected.equals(actual)) return;
        String expectedAdjusted = expected
                .replace("\r", "")
                .replace("\n", "")
                .replaceAll("> *", ">")
                .replaceAll(" *<", "<")
                .replaceAll(" +", " ");
        String actualAdjusted = actual
                .replace("\r", "")
                .replace("\n", "")
                .replaceAll("> *", ">")
                .replaceAll(" *<", "<")
                .replaceAll(" +", " ");
        ;
        if (expectedAdjusted.equals(actualAdjusted)) return;
        assertEquals(expectedAdjusted, actualAdjusted);
    }

}
