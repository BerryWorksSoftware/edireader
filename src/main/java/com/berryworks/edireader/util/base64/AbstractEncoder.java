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

import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;

/**
 * Encodes any sequence of 8-bit bytes into a base-64 encoding of those bytes.
 * In the encoded form, each byte contains only 6 bits of data from the
 * original data stream. Therefore there is an 3-to-4 expansion factor in the
 * expansion process.
 */
public abstract class AbstractEncoder extends AbstractEncoderDecoder {

    private final EncoderFrontEnd frontEnd;
    private final EncoderBackEnd backEnd;

    private static final int BUFFER_SIZE = 1000;

    protected AbstractEncoder() {
        frontEnd = new EncoderFrontEnd() {
            @Override
            protected void emit(byte value) {
                backEnd.consume(value);
            }

            @Override
            protected void endOfData() {
                backEnd.endOfData();
            }
        };
        final AbstractEncoderDecoder thisEncoder = this;
        backEnd = new EncoderBackEnd() {
            @Override
            protected void emit(byte b) {
                thisEncoder.emit(b);
            }
        };
    }

    @Override
    public void consume(byte b) {
        frontEnd.consume(b);
    }

    @Override
    protected void endOfData() {
        frontEnd.endOfData();
    }

    /**
     * Reads bytes from an input stream and directs their encoding
     * into a sequence of bytes according to the base-64 encoding conventions.
     *
     * @param inputStream stream of chars to be encoded
     * @throws IOException if an error occurs reading the inputStream
     */
    public void encode(InputStream inputStream) throws IOException {
        reset();
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            int n = inputStream.read(buffer);

            if (n < 0) {
                endOfData();
                break;
            } else if (n > 0)
                for (int i = 0; i < n; i++)
                    consume(buffer[i]);
        }
    }


    /**
     * Directs the encoding of arbitrary 8-bit bytes
     * into a sequence of bytes according to the base-64 encoding conventions.
     *
     * @param bytes to be encoded
     */
    public void encode(byte[] bytes) {
        reset();
        for (byte aByte : bytes) consume(aByte);
        endOfData();
    }

    public void encode(String s) {
        CharBuffer charBuffer = CharBuffer.wrap(s);
        encode(charset.encode(charBuffer).array());
    }

    @Override
    protected void reset() {
        frontEnd.reset();
        backEnd.reset();
    }

}
