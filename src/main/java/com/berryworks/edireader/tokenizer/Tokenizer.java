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

import com.berryworks.edireader.EDISyntaxException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface Tokenizer extends SourcePosition {
    String WHITESPACE = "\n\r \\";

    char getTerminator();

    void setTerminator(char d);

    char getDelimiter();

    void setDelimiter(char d);

    char getSubDelimiter();

    void setSubDelimiter(char sd);

    int getRepetitionSeparator();

    void setRepetitionSeparator(int e);

    void setRelease(int e);

    boolean hasMoreTokens() throws IOException, EDISyntaxException;

    Token nextToken() throws IOException, EDISyntaxException;

    String nextSimpleValue() throws SAXException, IOException;

    List<String> nextCompositeElement() throws IOException, EDISyntaxException;

    List<String> nextCompositeElement(boolean returnNullAtSegmentEnd) throws IOException, EDISyntaxException;

    String nextSimpleValue(boolean required) throws SAXException,
            IOException;

    int nextIntValue() throws SAXException, IOException;

    int nextIntValue(boolean returnZeroIfEmpty) throws SAXException, IOException;

    String nextSegment() throws SAXException, IOException;

    String getRecording();

    void setRecorder(boolean b);

    /**
     * Look ahead into the source of input chars and return the next n chars to
     * be seen, without disturbing the normal operation of getChar().
     *
     * @param n number of chars to return
     * @return char[] containing upcoming input chars
     * @throws IOException for problem reading EDI data
     */
    char[] lookahead(int n) throws IOException;

    /**
     * Returns any chars that have been read from the input stream but not yet
     * returned by getChar(). The use of lookahead() is a typical reason for
     * this to happen.
     *
     * @return char[] containing buffered chars of unused input
     */
    char[] getBuffered();

    char[] getChars(int n) throws IOException, EDISyntaxException;

    int getSegmentCount();

    int getElementInSegmentCount();

    void setWriter(Writer writer);

    Token skipSegment() throws SAXException, IOException;

    void ungetToken();

    String nextSimpleValue(boolean required, boolean returnNullAtSegmentEnd) throws SAXException,
            IOException;

    void scanTerminatorSuffix() throws IOException;

    boolean isEndOfData();

    char getSubSubDelimiter();

    void setSubSubDelimiter(char ssd);

    Reader getReader();

    void ungetChar();

    /**
     * Gets the next character of input.
     *
     * @throws IOException for problem reading EDI data
     */
    void getChar() throws IOException;
}
