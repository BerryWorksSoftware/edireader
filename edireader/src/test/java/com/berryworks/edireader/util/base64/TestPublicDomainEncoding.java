/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util.base64;

import com.berryworks.edireader.util.Conversion;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestPublicDomainEncoding {

    private byte[] byteArray;

    @Test
    public void testEqualsByteArray() {

        byteArray = new byte[]{0, 1, 2};

        assertFalse(equalsByteArray(null));
        assertTrue(equalsByteArray(new byte[]{0, 1, 2}));
        assertFalse(equalsByteArray(new byte[]{0, 1, 2, 3}));
        assertFalse(equalsByteArray(new byte[]{1, 1, 2}));
    }

    @Test
    public void testEncoding() {

        String string;

        byteArray = new byte[]{0, 1, 2};
        string = PublicDomainBase64.encodeBytes(byteArray);
        assertEquals("AAEC", string);
        assertTrue(equalsByteArray(PublicDomainBase64.decode(string)));

        byteArray = new byte[]{0, 1, 2, 3};
        string = PublicDomainBase64.encodeBytes(byteArray);
        assertEquals("AAECAw==", string);
        assertTrue(equalsByteArray(PublicDomainBase64.decode(string)));

        byteArray = new byte[]{0, 1, 2, 3, 4};
        string = PublicDomainBase64.encodeBytes(byteArray);
        assertEquals("AAECAwQ=", string);
        assertTrue(equalsByteArray(PublicDomainBase64.decode(string)));

        byteArray = toByteArray(new int[]{253, 254, 255, 254, 253});
        string = PublicDomainBase64.encodeBytes(byteArray);
        assertEquals("/f7//v0=", string);
        assertTrue(equalsByteArray(PublicDomainBase64.decode(string)));
    }

    @Test
    public void testConversion() {

        char[] charArray = new char[]{'a', 'b', 'c'};
        byteArray = Conversion.toByteArray(charArray);

        assertEquals(charArray.length, byteArray.length);
        assertEquals(97, byteArray[0]);
        assertEquals(98, byteArray[1]);
        assertEquals(99, byteArray[2]);

        byteArray = new byte[256];
        for (int i = 0; i < 256; i++)
            byteArray[i] = (byte) i;
        assertTrue(equalsByteArray(Conversion.toByteArray(Conversion.toCharArray(byteArray))));
    }

    boolean equalsByteArray(byte[] b) {

        if (b == null)
            return byteArray == null;
        if (byteArray == null)
            return false;

        if (byteArray.length != b.length)
            return false;

        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != b[i])
                return false;
        }
        return true;
    }

    private byte[] toByteArray(int[] ints) {
        byte[] result = new byte[ints.length];
        for (int i = 0; i < ints.length; i++)
            result[i] = (byte) ints[i];
        return result;
    }

}
