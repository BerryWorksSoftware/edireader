/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util.base64;

import com.berryworks.edireader.util.Conversion;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TestDecoding {

    private byte[] bytes;
    private AbstractDecoder decoder;
    private DecoderFrontEnd frontEnd;
    private DecoderBackEnd backEnd;
    private List<Integer> emission;

    @Before
    public void setUp() {
        decoder = new AbstractDecoder() {
            protected void emit(byte b) {
                emission.add((int) b);
            }
        };
        frontEnd = new DecoderFrontEnd() {
            protected void emit(byte b) {
                emission.add((int) b);
            }

            protected void endOfData() {
            }
        };
        backEnd = new DecoderBackEnd() {
            protected void emit(byte b) {
                emission.add((int) b);
            }
        };
        emission = new ArrayList<>();
    }

    @Test
    public void testCase1() throws IOException {
/**
 * Original bytes:          10101010  |   10101010  |  10101010   |   10101010  |   10101010  |   10101010
 * Emitted by front end:  101010 | 10 | 1010 | 1010 | 10 | 101010 | 101010 | 10 | 1010 | 1010 | 10 | 101010
 * assembled by back end: 101010 |  101010   |  101010   | 101010 | 101010 |  101010   |  101010   | 101010
 * encoding:                q    |    q      |    q      |   q    |   q    |    q      |    q      |   q
 * decoding first step:   101010 |  101010   |  101010   | 101010 | 101010 |  101010   |  101010   | 101010
 * decoding front end:    101010 | 10 | 1010 | 1010 | 10 | 101010 | 101010 | 10 | 1010 | 1010 | 10 | 101010
 * Emitted by back end:     10101010  |   10101010  |  10101010   |   10101010  |   10101010  |   10101010
 */
        bytes = Conversion.toByteArray("qqqqqqqq".toCharArray());
        for (byte aByte : bytes) frontEnd.consume(aByte);

        bytes = toByteArray(new int[]{42, 2, 10, 10, 2, 42, 42, 2, 10, 10, 2, 42});
        assertEqualsByteArray(bytes, resetEmission());

        for (byte aByte : bytes) backEnd.consume(aByte);

        assertEqualsByteArray(new int[]{170, 170, 170, 170, 170, 170}, resetEmission());
    }

    @Test
    public void testCase1b() throws IOException {
        decoder.decode("qqqqqqqq");
        assertEqualsByteArray(new int[]{170, 170, 170, 170, 170, 170}, resetEmission());
    }

    @Test
    public void testCase2() throws IOException {
/**
 * Original bytes:        00000000    | 00000001    | 0000010     | 00000011
 * Emitted by front end:  000000 | 00 | 0000 | 0001 | 00 | 000010 | 000000 | 11
 * assembled by back end: 000000 | 00 0000   | 0001 00   | 000010 | 000000 | 11 0000   | = | =
 * encoding:                A    |    A      |    E      |   C    |   A    |    w      | = | =
 * decoding first step:   000000 | 00 0000   | 0001 00   | 000010 | 000000 | 11 0000
 * decoding front end:    000000 | 00 | 0000 | 0001 | 00 | 000010 | 000000 | 11 | 0000
 * Emitted by back end:     00000000  |   00000001  |  0000010    |   00000011
 */
        bytes = Conversion.toByteArray("AAECAw==".toCharArray());
        for (byte aByte : bytes) frontEnd.consume(aByte);

        bytes = toByteArray(new int[]{0, 0, 0, 1, 0, 2, 0, 3, 0});
        assertEqualsByteArray(bytes, resetEmission());

        for (byte aByte : bytes) backEnd.consume(aByte);

        bytes = toByteArray(new int[]{0, 1, 2, 3});
        assertEqualsByteArray(bytes, resetEmission());
    }

    @Test
    public void testCase2b() throws IOException {
        decoder.decode("AAECAw==");
        assertEqualsByteArray(new byte[]{0, 1, 2, 3}, resetEmission());
    }

    @Test
    public void testCase3() throws IOException {
/**
 * Original bytes:        11111101    | 11111110    | 11111111    | 11111110    | 11111101
 * Emitted by front end:  111111 | 01 | 1111 | 1110 | 11 | 111111 | 111111 | 10 | 1111 | 1101
 * assembled by back end: 111111 | 01 1111   | 1110 11   | 111111 | 111111 | 10 1111   | 1101 00   | =
 * encoding:                /    |    f      |    7      |   /    |   /    |    v      |   0       | =
 * decoding first step:   111111 | 01 1111   | 1110 11   | 111111 | 111111 | 10 1111   | 1101 00
 * decoding front end:    111111 | 01 | 1111 | 1110 | 11 | 111111 | 111111 | 10 | 1111 | 1101 | 00
 * Emitted by back end:     11111101  |   11111110  |   11111111  |   11111110  |   11111101
 */
        bytes = Conversion.toByteArray("/f7//v0=".toCharArray());
        for (byte aByte : bytes) frontEnd.consume(aByte);

        bytes = new byte[]{63, 1, 15, 14, 3, 63, 63, 2, 15, 13, 0};
        assertEqualsByteArray(bytes, resetEmission());

        for (byte aByte : bytes) backEnd.consume(aByte);

        assertEqualsByteArray(new int[]{253, 254, 255, 254, 253}, resetEmission());
    }

    @Test
    public void testCase3b() throws IOException {
        decoder.decode("/f7//v0=");
        assertEqualsByteArray(new int[]{253, 254, 255, 254, 253}, resetEmission());
    }

    @Test
    public void testEachEncodeVariation() throws IOException {
        bytes = Conversion.toByteArray("/f7//v0=".toCharArray());
        decoder.decode(new ByteArrayInputStream(bytes));
        assertEqualsByteArray(new int[]{253, 254, 255, 254, 253}, resetEmission());

        decoder.decode(bytes);
        assertEqualsByteArray(new int[]{253, 254, 255, 254, 253}, resetEmission());

        decoder.decode("MDEyMzQ1Njc4OUFiQ2RFZgoNCQ==");
        assertEqualsByteArray(
                Conversion.toByteArray("0123456789AbCdEf\n\r\t".toCharArray()),
                resetEmission());
    }

    public void assertEqualsByteArray(String string, List<Integer> list) {
        assertEqualsByteArray(toByteArray(string), list);
    }

    public void assertEqualsByteArray(byte[] bytes, List list) {
        assertEquals("array length does not match List size:  ", bytes.length, list.size());
        Iterator it = list.iterator();
        for (int i = 0; i < bytes.length; i++) {
            Object o = it.next();
            if (o instanceof Integer) {
                Integer iValue = (int) bytes[i];
                char[] c = Conversion.toCharArray(new byte[]{(byte) iValue.intValue()});
                char[] d = Conversion.toCharArray(new byte[]{((Integer) o).byteValue()});
                assertEquals("mismatched values at index " + i + ":  (expecting char " + d[0] + " instead of " + c[0] + "): ", iValue, o);
            } else {
                throw new RuntimeException("Type " + o.getClass().getName() + " not supported in assertEqualsByteArra()");
            }
        }
    }

    public void assertEqualsByteArray(int[] ints, List list) {
        assertEqualsByteArray(toByteArray(ints), list);
    }

    private byte[] toByteArray(int[] ints) {
        byte[] result = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            result[i] = (byte) ints[i];
        return result;
    }

    private byte[] toByteArray(String s) {
        return Conversion.toByteArray(s.toCharArray());
    }

    private List resetEmission() {
        List result = emission;
        emission = new ArrayList<>();
        return result;
    }

}