/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class Conversion {

    private static final Charset charset = Charset.forName("8859_1");

    public static byte[] toByteArray(char[] data) {
        return toByteArray(data, 0, data.length);
    }

    public static byte[] toByteArray(char[] data, int offset, int length) {
        CharBuffer charBuffer = CharBuffer.wrap(data, offset, length);
        return charset.encode(charBuffer).array();
    }

    public static char[] toCharArray(byte[] data) {
        return toCharArray(data, 0, data.length);
    }

    public static char[] toCharArray(byte[] data, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data, offset, length);
        return charset.decode(byteBuffer).array();
    }
}
