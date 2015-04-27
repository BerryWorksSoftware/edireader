/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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


public class TestEncoding {

    private byte[] inputBytes;
    private byte[] intermediateBytes;
    private String expectedEncoding;
    private AbstractEncoder abstractEncoder;
    private EncoderFrontEnd frontEnd;
    private EncoderBackEnd backEnd;
    private List emission;

    @Before
    public void setUp() {
        abstractEncoder = new AbstractEncoder() {
            @Override
            protected void emit(byte b) {
                emission.add((int) b);
            }
        };
        frontEnd = new EncoderFrontEnd() {
            @Override
            protected void emit(byte b) {
                emission.add((int) b);
            }

            @Override
            protected void endOfData() {
            }
        };
        backEnd = new EncoderBackEnd() {
            @Override
            protected void emit(byte b) {
                emission.add((int) b);
            }
        };
        emission = new ArrayList();
    }

    @Test
    public void testCase1() throws IOException {
/**
 * Original bytes:        10101010    | 10101010    | 10101010    | 10101010    | 10101010    | 10101010
 * Emitted by front end:  101010 | 10 | 1010 | 1010 | 10 | 101010 | 101010 | 10 | 1010 | 1010 | 10 | 101010
 * assembled by back end: 101010 | 10 1010   | 1010 10   | 101010 | 101010 | 10 1010   | 1010 10   | 101010
 */
        inputBytes = toByteArray(new int[]{170, 170, 170, 170, 170, 170});
        for (byte inputByte : inputBytes) frontEnd.consume(inputByte);

        intermediateBytes = toByteArray(new int[]{42, 2, 10, 10, 2, 42, 42, 2, 10, 10, 2, 42});
        assertEqualsByteArray(intermediateBytes, resetEmission());

        for (byte intermediateByte : intermediateBytes) backEnd.consume(intermediateByte);

        expectedEncoding = "qqqqqqqq";
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode(new ByteArrayInputStream(inputBytes));
        assertEqualsByteArray(expectedEncoding, resetEmission());
    }

    @Test
    public void testCase2() throws IOException {
/**
 * Original bytes:        00000000    | 00000001    | 0000010     | 00000011
 * Emitted by front end:  000000 | 00 | 0000 | 0001 | 00 | 000010 | 000000 | 11
 * assembled by back end: 000000 | 00 0000   | 0001 00   | 000010 | 000000 | 11 0000 | = | =
 */
        inputBytes = new byte[]{0, 1, 2, 3};
        for (byte inputByte : inputBytes) frontEnd.consume(inputByte);

        intermediateBytes = toByteArray(new int[]{0, 0, 0, 1, 0, 2, 0, 3});
        assertEqualsByteArray(intermediateBytes, resetEmission());

        for (byte intermediateByte : intermediateBytes) backEnd.consume(intermediateByte);
        backEnd.endOfData();

        expectedEncoding = "AAECAw==";
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode(new ByteArrayInputStream(inputBytes));
        assertEqualsByteArray(expectedEncoding, resetEmission());
    }

    @Test
    public void testCase3() throws IOException {
/**
 * Original bytes:        11111101    | 11111110    | 11111111    | 11111110    | 11111101
 * Emitted by front end:  111111 | 01 | 1111 | 1110 | 11 | 111111 | 111111 | 10 | 1111 | 1101
 * assembled by back end: 111111 | 01 1111   | 1110 11   | 111111 | 111111 | 10 1111   | 1101 00 | =
 */
        inputBytes = toByteArray(new int[]{253, 254, 255, 254, 253});
        for (byte inputByte : inputBytes) frontEnd.consume(inputByte);

        intermediateBytes = toByteArray(new int[]{63, 1, 15, 14, 3, 63, 63, 2, 15, 13});
        assertEqualsByteArray(intermediateBytes, resetEmission());

        for (byte intermediateByte : intermediateBytes) backEnd.consume(intermediateByte);
        backEnd.endOfData();

        expectedEncoding = "/f7//v0=";
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode(new ByteArrayInputStream(inputBytes));
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode(inputBytes);
        assertEqualsByteArray(expectedEncoding, resetEmission());
    }

    @Test
    public void testEachEncodeVariation() throws IOException {
        inputBytes = toByteArray(new int[]{253, 254, 255, 254, 253});
        abstractEncoder.encode(new ByteArrayInputStream(inputBytes));
        expectedEncoding = "/f7//v0=";
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode(inputBytes);
        assertEqualsByteArray(expectedEncoding, resetEmission());

        abstractEncoder.encode("0123456789AbCdEf\n\r\t");
        assertEqualsByteArray(
                "MDEyMzQ1Njc4OUFiQ2RFZgoNCQ==",
                resetEmission());
    }

    public void assertEqualsByteArray(String string, List list) {
        assertEqualsByteArray(toByteArray(string), list);
    }

    public void assertEqualsByteArray(byte[] bytes, List list) {
        assertEquals("array length does not match List size:  ", bytes.length, list.size());
        Iterator it = list.iterator();
        for (int i = 0; i < bytes.length; i++) {
            Object o = it.next();
            if (o instanceof Integer) {
                Integer iValue = (int) bytes[i];
                char c[] = Conversion.toCharArray(new byte[]{(byte) iValue.intValue()});
                char d[] = Conversion.toCharArray(new byte[]{((Integer) o).byteValue()});
                assertEquals("mismatched values at index " + i + ":  (expecting char " + d[0] + " instead of " + c[0] + "): ", iValue, o);
            } else {
                throw new RuntimeException("Type " + o.getClass().getName() + " not supported in assertEqualsByteArra()");
            }
        }
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
        emission = new ArrayList();
        return result;
    }

}
