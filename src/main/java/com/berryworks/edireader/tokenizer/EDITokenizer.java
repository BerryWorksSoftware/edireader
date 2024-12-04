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

package com.berryworks.edireader.tokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.nio.Buffer;
import java.nio.CharBuffer;

/**
 * Interprets EDI input as a sequence of primitive syntactic tokens.
 * <p>
 * As an EDI interchange is parsed, the parser uses a Tokenizer to advance through the
 * input EDI stream one token at a time. A call to <code>nextToken()</code> causes the tokenizer to advance
 * past the next token and return a <code>Token</code> instance describing that token.
 * <p>
 * This implementation of Tokenizer uses CharBuffer instead of char[].
 */
public class EDITokenizer extends AbstractTokenizer {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    public static final int BUFFER_SIZE = 1000;
    private final CharBuffer charBuffer = CharBuffer.wrap(new char[BUFFER_SIZE]);

    public EDITokenizer(Reader source) {
        super(source);
        ((Buffer) charBuffer).flip();
//        logger.debug("Constructed a new EDITokenizer");
    }

    public EDITokenizer(Reader source, char[] preRead) {
        this(source);
        if (preRead == null || preRead.length == 0)
            return;

        if (preRead.length > charBuffer.capacity())
            throw new RuntimeException("Attempt to create EDITokenizer with " + preRead.length +
                    " pre-read chars, which is greater than the internal buffer size of " + charBuffer.capacity());
        ((Buffer) charBuffer).clear();
        charBuffer.put(preRead);
        ((Buffer) charBuffer).flip();
    }

    /**
     * Returns a String representation of the current state of the tokenizer
     * for testing and debugging purposes.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        String result = "tokenizer state:";
        result += " segmentCount=" + segmentCount;
        result += " charCount=" + charCount;
        result += " segTokenCount=" + segTokenCount;
        result += " segCharCount=" + segCharCount;
        result += " currentToken=" + currentToken;
        result += " buffer.limit=" + charBuffer.limit();
        result += " buffer.position=" + charBuffer.position();
        return result;
    }

    /**
     * Gets the next character of input. Sets cChar and cClass
     *
     * @throws IOException for problem reading EDI data
     */
    public void getChar() throws IOException {
        if (unGot) {
            // The current character has been "put back" with ungetChar()
            // after having been seen with getChar(). Therefore, this call
            // to getChar() can simply reget the current character.
            unGot = false;
            charCount++;
            segCharCount++;
            return;
        }

        // Read a fresh character from the input source.
        // But first copy the current one to an outputWriter
        // or the recorder if necessary.
        if (outputWriter != null) {
            // We do have an outputWriter wanting data, but do we have
            // a current character to write? And make sure writing is
            // not suspended.
            if ((!endOfFile) && (!writingSuspended))
                outputWriter.write(cChar);
        }
        if (recorderOn)
            recording.append(cChar);

        if (charBuffer.remaining() == 0) {
            readUntilBufferProvidesAtLeast(1);
        }

        if (endOfFile) {
            cClass = CharacterClass.EOF;
//            logger.debug("end-of-file encountered");
        } else {
            cChar = charBuffer.get();
            if (cChar == delimiter)
                cClass = CharacterClass.DELIMITER;
            else if (cChar == terminator)
                cClass = CharacterClass.TERMINATOR;
            else if (cChar == subDelimiter)
                cClass = CharacterClass.SUB_DELIMITER;
            else if (cChar == release)
                cClass = CharacterClass.RELEASE;
            else if (cChar == repetitionSeparator)
                cClass = CharacterClass.REPEAT_DELIMITER;
            else
                cClass = CharacterClass.DATA;
        }
        charCount++;
        segCharCount++;
    }

    /**
     * Gets the remaining chars that have been read into the buffer
     * and not returned by getChars(n) or equivalant. Chars previewed
     * by lookahead(n) are not considered to have been used and therefore
     * are included among the chars returned by getBuffered.
     * <p>
     * The use of getBuffered() is intended for only very special situations.
     * For example, if an input stream contains multiple fully independent EDI
     * interchanges -- perhaps from different EDI standards -- it is useful to
     * logically "start from scratch" on each successive interchange, with new
     * parser, tokenizer, buffer, etc, with any chars remaining in the buffer
     * from the previous interchange to be used as new data.
     *
     * @return chars of unprocessed input data
     */
    public char[] getBuffered() {
        char[] result = new char[0];

        if (charBuffer.remaining() == 0 && !unGot) {
            return result;
        }

        try {
            int n = charBuffer.remaining();
            if (endOfFile && n == 0) {
                // Special case: if we've hit eof and the charBuffer is empty
                // ignore an unGot char if there is one.
            } else {
                n += unGot ? 1 : 0;
            }
            result = lookahead(n);
        } catch (Exception ignore) {
        }

        return result;
    }

    /**
     * Look ahead into the source of input chars and return the next n chars to
     * be seen, without disturbing the normal operation of getChar().
     *
     * @param n number of chars to return
     * @return char[] containing upcoming input chars
     * @throws IOException for problem reading EDI data
     */
    public char[] lookahead(int n) throws IOException {
//        logger.debug("EDITokenizer.lookahead({})", n);
        char[] rval = new char[n];

        // The 1st char is grabbed using the tokenizer's built-in getChar() / ungetChar() mechanism.
        // This allows things to work properly whether or not the next char has already been gotten.
        getChar();
        rval[0] = cChar;
        ungetChar();

        // The minus 1 is because we have already filled the first char of the return value, so we only need n-1 more
        if (charBuffer.remaining() < n - 1) {
//            logger.debug("Buffering more data to satisfy lookahead({}})", n);
            readUntilBufferProvidesAtLeast(n - 1);
        }

        // Move chars from the buffer into the return value
        int j = 1;
        for (int i = charBuffer.position(); i < charBuffer.limit() && j < n; i++)
            rval[j++] = charBuffer.get(i);

        // If more lookahead chars were requested than were satisfied for any reason,
        // then fill the return value with '?' to the requested length.
        while (j < n) {
            rval[j++] = '?';
        }

        return rval;
    }

    private void readUntilBufferProvidesAtLeast(int needed) throws IOException {

        int remaining;
        while ((remaining = charBuffer.remaining()) < needed) {
//            logger.debug("Reading from input stream because at least {} chars are needed and only {} are available",
//                    needed, remaining);
            charBuffer.compact();
            int n;
            while ((n = inputReader.read(charBuffer)) == 0) {
            }
            ((Buffer) charBuffer).flip();

            if (n < 0) {
//                logger.debug("Hit end of file on the input stream");
                endOfFile = true;
                break;
            } else {
//                logger.debug("Number of chars read from input stream: {}", n);
            }
        }
    }
}