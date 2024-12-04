/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Conversion {

    private static final Charset charset = StandardCharsets.ISO_8859_1;

    public static void ediToxml(Reader ediInput, Writer xmlOutput, EDIReader parser) throws TransformerException {
        InputSource inputSource = new InputSource(ediInput);
        SAXSource source = new SAXSource(parser, inputSource);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(xmlOutput);
        transformer.transform(source, result);
    }

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
