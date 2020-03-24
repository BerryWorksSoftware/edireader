/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader.util.base64;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Provides for base-64 decoding of Java Strings.
 */
public class StringBase64Decoder extends AbstractDecoder {
    private final StringBuilder stringBuffer = new StringBuilder();
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(100);

    @Override
    protected void emit(byte b) {
        if (!byteBuffer.hasRemaining()) {
            feedStringBuffer();
            byteBuffer.clear();
        }
        byteBuffer.put(b);
    }

    private void feedStringBuffer() {
        ((Buffer) byteBuffer).flip();
        stringBuffer.append(charset.decode(byteBuffer));
    }

    /**
     * Decodes the characters in a base-64 encoded String, producing the
     * original data as a String.
     *
     * @param encodedText - the base-64 encoded text that is to be decoded
     * @return decoded text
     */
    public String decodeAsString(String encodedText) {
        CharBuffer charBuffer = CharBuffer.wrap(encodedText);

        // Allocate a modest sized non-direct ByteBuffer to receive the
        // bytes as they are encoded from the String
        ByteBuffer base64ByteBuffer = ByteBuffer.allocate(100);

        // Use the encoder repeatedly until all of the chars have been encoded as bytes
        // and presented as input for base 64 encoding.
        CharsetEncoder encoder = charset.newEncoder();
        while (true) {
            CoderResult coderResult = encoder.encode(charBuffer, base64ByteBuffer, true);
            if (coderResult.isError()) {
                throw new RuntimeException("Unrecoverable failure in Base64 encoding");
            }
            ((Buffer) base64ByteBuffer).flip();
            while (base64ByteBuffer.hasRemaining()) {
                consume(base64ByteBuffer.get());
            }
            if (coderResult.isUnderflow()) {
                break;
            }
            base64ByteBuffer.clear();
        }
        endOfData();
        feedStringBuffer();

        return stringBuffer.toString();
    }
}
