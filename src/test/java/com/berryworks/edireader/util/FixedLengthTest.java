/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixedLengthTest {

    @Test
    public void testSpaces() {
        assertEquals("", FixedLength.spaces(0));
        assertEquals(" ", FixedLength.spaces(1));
        assertEquals("     ", FixedLength.spaces(5));
//        assertEquals("", FixedLength.spaces(-1));
    }

    @Test(expected = RuntimeException.class)
    public void testTooManySpaces() {
        FixedLength.spaces(1000);
    }

    @Test
    public void testZeroes() {
        assertEquals("", FixedLength.zeroes(0));
        assertEquals("0", FixedLength.zeroes(1));
        assertEquals("00000", FixedLength.zeroes(5));
//        assertEquals("", FixedLength.zeroes(-1));
    }

    @Test(expected = RuntimeException.class)
    public void testTooManyZeroes() {
        FixedLength.zeroes(1000);
    }

    @Test
    public void testValueOfInt() {
        assertEquals("1", FixedLength.valueOf(1, 1));
        assertEquals("001", FixedLength.valueOf(1, 3));
        assertEquals("", FixedLength.valueOf(5, 0));
        assertEquals("05", FixedLength.valueOf(5, 2));
    }

    @Test
    public void testValueOfString() {
        assertEquals(" ", FixedLength.valueOf(null, 1));
        assertEquals("  ", FixedLength.valueOf(null, 2));
        assertEquals("1", FixedLength.valueOf("1", 1));
        assertEquals("1  ", FixedLength.valueOf("1", 3));
        assertEquals("", FixedLength.valueOf("5", 0));
        assertEquals("5 ", FixedLength.valueOf("5", 2));
        assertEquals("55", FixedLength.valueOf("5555", 2));
    }

    @Test(expected = RuntimeException.class)
    public void testTooLong() {
        String s = FixedLength.valueOf(1, 13);
    }

    @Test
    public void testLeadingZeros() {
        assertEquals("0001", FixedLength.leadingZeros("1", 4));
        assertEquals("0012", FixedLength.leadingZeros("12", 4));
        assertEquals("0123", FixedLength.leadingZeros("123", 4));
        assertEquals("1234", FixedLength.leadingZeros("1234", 4));
        assertEquals("1234", FixedLength.leadingZeros("12345", 4));
        assertEquals("0000", FixedLength.leadingZeros(null, 4));
        assertEquals("0000", FixedLength.leadingZeros("", 4));
        assertEquals("000 ", FixedLength.leadingZeros(" ", 4));
        assertEquals("00  ", FixedLength.leadingZeros("  ", 4));
        assertEquals("0000", FixedLength.leadingZeros("0", 4));
        assertEquals("0 0 ", FixedLength.leadingZeros(" 0 ", 4));
    }
}

