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

import com.berryworks.edireader.EDIAbstractReader;
import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.error.ErrorMessages;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTokenizer implements Tokenizer, ErrorMessages {

    protected enum State {
        EXPECTING_SEGMENT, IN_SEGMENT, IN_COMPOSITE
    }

    protected enum CharacterClass {
        DATA, DELIMITER, SUB_DELIMITER, RELEASE, TERMINATOR, REPEAT_DELIMITER, EOF
    }

    protected CharacterClass cClass;
    protected State state;
    protected Writer outputWriter;
    protected boolean writingSuspended;
    protected final StringBuilder recording = new StringBuilder();
    protected boolean recorderOn;

    protected int segmentCount;
    protected int segTokenCount;
    protected int charCount;
    protected int segCharCount;
    protected final Reader inputReader;

    protected char delimiter = '+';
    protected char subDelimiter = ':';
    protected char subSubDelimiter = '&';

    // release is an int instead of a char so that it can hold
    // a char value (as a positive int) or an indicator of
    // "no release char" (an int value of -1).
    protected int release = -1;

    // repetitionSeparator is an int instead of a char so that it can hold
    // a char value (as a positive int) or an indicator of
    // "no repeating fields" (an int value of -1)
    protected int repetitionSeparator = -1;

    protected char terminator = '.';
    protected boolean tokenReady;
    protected boolean repetition;
    protected boolean endOfFile;
    protected final Token currentToken;
    protected char cChar;
    protected boolean unGot;


    /**
     * Gets the count of segments that have been read or partially read.
     *
     * @return The segmentCount value
     */
    public int getSegmentCount() {
        return segmentCount;
    }

    public int getElementInSegmentCount() {
        return segTokenCount;
    }

    public int getCharCount() {
        return charCount;
    }

    public int getSegmentCharCount() {
        return segCharCount;
    }

    @Override
    public void setCharCounts(int charCount, int segmentCharCount) {
        this.charCount = charCount;
        this.segCharCount = segmentCharCount;
    }

    public Reader getReader() {
        return inputReader;
    }

    public char getSubSubDelimiter() {
        return subSubDelimiter;
    }

    public void setSubSubDelimiter(char ssd) {
        subSubDelimiter = ssd;
    }

    /**
     * Sets the release character
     *
     * @param e The new release value
     */
    public void setRelease(int e) {
        release = e;
    }

    /**
     * Gets the character used to delimit repeating fields.
     *
     * @return The repetition char, or -1 if no repetition char is in effect
     */
    public int getRepetitionSeparator() {
        return repetitionSeparator;
    }

    /**
     * Sets the character used to delimit repeating fields.
     *
     * @param e The new value
     */
    public void setRepetitionSeparator(int e) {
        // In EDITokenizer, -1 for a repetition char means that none is in effect.
        // An attempt to set it to zero is interpreted as an alternate way to indicate
        // that no repetition char is in effect, so we set the value to -1 for that
        // case as well.
        repetitionSeparator = e > 0 ? e : -1;
    }

    public void setTerminator(char d) {
        terminator = d;
    }

    public char getTerminator() {
        return terminator;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char d) {
        delimiter = d;
    }

    public char getSubDelimiter() {
        return subDelimiter;
    }

    public void setSubDelimiter(char sd) {
        subDelimiter = sd;
    }

    public void copy(char c) {
        if (outputWriter != null)
            try {
                outputWriter.write(c);
            } catch (IOException e) {
                // ignore
            }
    }

    public boolean isEndOfData() {
        return endOfFile;
    }

    /**
     * Returns the value of the next token, expected to be of type SIMPLE or
     * EMPTY. If <code>required</code> is true, then it may not be EMPTY. A
     * syntax exception is thrown for any other types.
     *
     * @param required               an EMPTY token is not allowed
     * @param returnNullAtSegmentEnd governs behavior at end of segment
     * @return String value of the token
     * @throws SAXException unexpected tokens
     * @throws IOException  for problem reading EDI data
     */
    public String nextSimpleValue(boolean required, boolean returnNullAtSegmentEnd) throws SAXException,
            IOException {
        Token t = nextToken();
        switch (t.getType()) {
            case EMPTY:
                if (required)
                    throw new EDISyntaxException("Mandatory element missing in "
                            + t.getSegmentType() + " segment", this);
                break;
            case SEGMENT_END:
                if (required)
                    throw new EDISyntaxException("Mandatory element missing in "
                            + t.getSegmentType() + " segment", this);
                else if (returnNullAtSegmentEnd)
                    return null;
                else
                    break;
            case SIMPLE:
                break;
            default:
                throw new EDISyntaxException(EXPECTED_SIMPLE_TOKEN, this);
        }
        return t.getValue();
    }

    /**
     * Returns the value of the next token, expected to be of type SIMPLE or
     * EMPTY. If <code>required</code> is true, then it may not be EMPTY. A
     * syntax exception is thrown for any other types.
     *
     * @param required an EMPTY token is not allowed
     * @return String value of the token
     * @throws SAXException unexpected tokens
     * @throws IOException  for problem reading EDI data
     */
    public String nextSimpleValue(boolean required) throws SAXException,
            IOException {
        return nextSimpleValue(required, false);
    }

    /**
     * Equivalent to <code>nextSimpleValue(true)</code>
     *
     * @return String value of the token
     * @throws SAXException unexpected tokens
     * @throws IOException  for problem reading EDI data
     */
    public String nextSimpleValue() throws SAXException, IOException {
        return nextSimpleValue(true);
    }

    /**
     * Gets the next token expecting it to be a digit sequence.
     * <p/>
     *
     * @return value integer value implied by digits
     * @throws SAXException                                for SAX compatibility
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if contains non-digits
     *                                                     if empty
     */
    public int nextIntValue() throws SAXException, IOException {
        int i;
        try {
            i = Integer.parseInt(nextSimpleValue());
        } catch (NumberFormatException e) {
            throw new EDISyntaxException(DIGITS_ONLY, this);
        }
        return i;
    }

    /**
     * Parses the next token expecting to find a composite element - one
     * composed of subelements separated by the subElementDelimiter.
     *
     * @return subelements as a Vector of Strings
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if invalid EDI is detected
     */
    public List<String> nextCompositeElement() throws IOException, EDISyntaxException {
        return nextCompositeElement(false);
    }

    public List<String> nextCompositeElement(boolean returnNullAtSegmentEnd) throws IOException, EDISyntaxException {
        List<String> result = new ArrayList<>();
        loop:
        while (true) {
            Token t = nextToken();
            switch (t.getType()) {
                case SUB_ELEMENT:
                    // add this token's value to the list and
                    // others that follow it
                    result.add(t.getValue());
                    if (t.isLast()) break loop;
                    break;
                case SUB_EMPTY:
                    result.add("");
                    if (t.isLast()) break loop;
                    break;
                case SIMPLE:
                    // We saw a simple token terminated by a normal
                    // element delimiter, not the subElement delimiter.
                    // Treat this as a composite element with only one
                    // value.
                    result.add(t.getValue());
                    break loop;
                case EMPTY:
                    // An empty token terminated by
                    // a normal element delimiter, segment end, etc.
                    // Treat this as a composite element with no values
                    // by returning an empty List.
                    break loop;
                case SEGMENT_END:
                    if (returnNullAtSegmentEnd)
                        return null;
                    break loop;
                default:
                    throw new EDISyntaxException(INVALID_COMPOSITE, this);
            }
        }
        return result;
    }

    /**
     * Arranges for the token most recently returned by <code>nextToken</code>
     * to be returned again if a future call to <code>nextToken</code>.
     */
    public void ungetToken() {
        tokenReady = true;
    }

    /**
     * Arranges for getChar() to see the current char again the next time it is
     * called, in effect "putting back" that char to be seen again.
     */
    public void ungetChar() {
        unGot = true;
        charCount--;
        segCharCount--;
    }

    /**
     * Returns the next Token from the InputSource, or null if there are no more
     * tokens.
     *
     * @return Token
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if invalid EDI is detected
     */
    public Token nextToken() throws IOException, EDISyntaxException {
        if (!tokenReady)
            advance();
        tokenReady = false;
        return currentToken;
    }

    /**
     * Peeks ahead to determine if nextToken() would find another token.
     *
     * @return true if there is another token available to nextToken()
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if invalid EDI is detected
     */
    public boolean hasMoreTokens() throws IOException, EDISyntaxException {
        if (!tokenReady)
            advance();
        return tokenReady;
    }

    /**
     * Skips over tokens until the beginning of a new segment is encountered.
     *
     * @return segType String containing value of leading field
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    public String nextSegment() throws SAXException, IOException {
        Token t;
        int i = 0;
        while (true) {
            t = nextToken();
            Token.TokenType tokenType = t.getType();
            if (tokenType == Token.TokenType.SEGMENT_START)
                break;
            if (tokenType == Token.TokenType.END_OF_DATA)
                throw new EDISyntaxException(UNEXPECTED_EOF, this);
            if (++i > 30)
                throw new EDISyntaxException("Too many fields for "
                        + t.getSegmentType()
                        + " segment (Segment terminator problem?)", this);
        }
        return t.getSegmentType();
    }

    /**
     * Skips over tokens until an END_SEGMENT token is reached, marking the end
     * of the current segment. This Tokenizer is therefore positioned so that
     * the next call to getToken() sees the first token after the end of this
     * segment.
     *
     * @return token SEGMENT_END
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    public Token skipSegment() throws SAXException, IOException {
        Token t;
        int i = 0;
        while (true) {
            t = nextToken();
            Token.TokenType tokenType = t.getType();
            if ((tokenType == Token.TokenType.SEGMENT_END) || (tokenType == Token.TokenType.END_OF_DATA))
                break;
            if (++i > 30)
                throw new EDISyntaxException("Too many fields in "
                        + t.getSegmentType() + " segment", this);
        }
        return t;
    }

    /**
     * Scans a series of data characters up to the first character other than a
     * data character.
     * <p/>
     * Each character is appended to the value of the current token. Upon
     * return, cChar and cClass are left referencing the character after the
     * last character of data; in other words, getChar() will have been called
     * seeing something other than a character of data. Release sequences are
     * handled within this method.
     *
     * @param limit maximum number of data characters allowed in the element
     * @return class of char that caused scan to stop
     * @throws IOException                                 problem reading EDI input
     * @throws com.berryworks.edireader.EDISyntaxException specific syntax error in parsed EDI data
     */
    protected CharacterClass scanData(int limit) throws IOException, EDISyntaxException {
        loop:
        while (true) {
            getChar();
            switch (cClass) {
                case RELEASE:
                    // Ignore this release character, but get the next
                    // character and treat it as data (by falling in
                    // to the following case) without regard to the class
                    // that character would naturally be.
                    getChar();
                case DATA:
                    if (--limit == 0)
                        throw new EDISyntaxException(ELEMENT_TOO_LONG, this);
                    currentToken.append(cChar);
                    break;
                case SUB_DELIMITER:
                    break loop;
                case REPEAT_DELIMITER:
                    repetition = true;
                    break loop;
                case TERMINATOR:
                    ungetChar();
                    // fall into the default logic below
                default:
                    repetition = false;
                    break loop;
            }
        }
        return cClass;
    }

    /**
     * Equivalent to scanData(infinite)
     *
     * @return class of char that caused scan to stop
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if invalid EDI is detected
     */
    protected CharacterClass scanData() throws IOException, EDISyntaxException {
        return scanData(0);
    }

    /**
     * Scans over a series of characters that, after a segment terminator, are
     * considered to be ignorable whitespace. This allows segments with formal
     * segment terminator characters to be followed be line-oriented characters
     * (line feeds and carriage returns).
     *
     * @throws IOException for problem reading EDI data
     */
    public void scanTerminatorSuffix() throws IOException {
        do {
            getChar();
        } while (cClass != CharacterClass.EOF && WHITESPACE.indexOf(cChar) != -1);
        ungetChar();
    }

    public char[] getChars(int n) throws IOException, EDISyntaxException {
        char[] result = new char[n];
        for (int i = 0; i < n; i++) {
            getChar();
            if (cClass == CharacterClass.EOF)
                throw new EDISyntaxException("Encountered end of data unexpectedly after reading " + i + " characters of an expected " + n + " character sequence");
            result[i] = cChar;
        }
        return result;
    }


    /**
     * The outputWriter provides the service of copying parsed data to an output
     * destination. This is particularly useful in splitting applications. This
     * service is optional; setWriter(null) turns copying off, which is the
     * default condition. In addition, copying can be suspended and then resumed
     * via the suspendWriting() method. Note that with writing suspended, the
     * copyToken() and similar methods are still available for manual copying of
     * data to the output destination.
     *
     * @param writer to receive a copy EDI data as it is read, if null then copying is disabled
     */
    public void setWriter(Writer writer) {
        outputWriter = writer;
    }

    /**
     * Return the recording.
     *
     * @return The recording value
     */
    public String getRecording() {
        return recording.toString();
    }


    /**
     * Shorthand for EDIReader.trace(String)
     *
     * @param string text message to appear in trace
     */
    protected void trace(String string) {
        EDIAbstractReader.trace(string);
    }

    /**
     * Used in conjunction with setWriter to temporarily suspend and then resume
     * copying parsed data to an output destination.
     *
     * @param b true suspends copying, false enables it again
     */
    public void suspendWriting(boolean b) {
        writingSuspended = b;
    }

    /**
     * Turn the recorder on (true) or off (false).
     *
     * @param b The new recorder value
     */
    public void setRecorder(boolean b) {
        recorderOn = b;
        if (EDIReader.debug)
            trace("recorder turned " + (b ? "on" : "off"));
    }

    public AbstractTokenizer(Reader source) {
        state = State.EXPECTING_SEGMENT;
        outputWriter = null;
        inputReader = source;
        tokenReady = false;
        currentToken = new TokenImpl(this);
    }

    /**
     * Advances to the next token. <pr>Sets tokenReady, currentToken, and state.
     *
     * @throws IOException                                 for problem reading EDI data
     * @throws com.berryworks.edireader.EDISyntaxException if invalid EDI is detected
     */
    protected void advance() throws IOException, EDISyntaxException {
        getChar();
        tokenReady = true;
        switch (cClass) {

            case RELEASE:
                // Ignore this release character, but get the next
                // character and treat it as data (by falling in
                // to the following case) without regard to the class
                // that character would naturally be.
                getChar();

            case DATA:
                switch (state) {
                    case IN_SEGMENT:
                        segTokenCount++;
                        currentToken.setType(Token.TokenType.SIMPLE);
                        currentToken.setValue(cChar);
                        if (!repetition)
                            currentToken.incrementIndex();
                        currentToken.resetSubElementIndex();
                        if (scanData() == CharacterClass.SUB_DELIMITER) {
                            // We have a composite token instead of a simple one
                            currentToken.setType(Token.TokenType.SUB_ELEMENT);
                            currentToken.setLast(false);
                            state = State.IN_COMPOSITE;
                        }
                        break;
                    case IN_COMPOSITE:
                        segTokenCount++;
                        currentToken.setType(Token.TokenType.SUB_ELEMENT);
                        currentToken.incrementSubElementIndex();
                        currentToken.setValue(cChar);
                        if (scanData() != CharacterClass.SUB_DELIMITER) {
                            // We hit something that marks the end of a series of subelements
                            state = State.IN_SEGMENT;
                            currentToken.setLast(true);
                        }
                        break;
                    default:
                        // We are at the beginning of a segment
                        segmentCount++;
                        segTokenCount = 1;
                        segCharCount = 1;
                        currentToken.setType(Token.TokenType.SEGMENT_START);
                        currentToken.setValue(cChar);
                        currentToken.resetIndexes();
                        scanData(10);
                        currentToken.setSegmentType(currentToken.getValue());
                        state = State.IN_SEGMENT;
                }
                break;

            case TERMINATOR:
                switch (state) {
                    case IN_COMPOSITE:
                        // return an empty subelement token, marked as last,
                        // before returning the segment terminator token.
                        currentToken.incrementSubElementIndex();
                        currentToken.setLast(true);
                        currentToken.setType(Token.TokenType.SUB_EMPTY);
                        currentToken.resetValue();
                        ungetChar();
                        // change state so that next time
                        // we will go down a different path.
                        state = State.IN_SEGMENT;
                        break;
                    default:
                        currentToken.setType(Token.TokenType.SEGMENT_END);
                        state = State.EXPECTING_SEGMENT;
                        scanTerminatorSuffix();
                        currentToken.resetSubElementIndex();
                }
                break;

            case DELIMITER:
                switch (state) {
                    case IN_COMPOSITE:
                        // return an empty subelement token, marked as last,
                        // before returning the delimiter token.
                        currentToken.incrementSubElementIndex();
                        currentToken.setType(Token.TokenType.SUB_EMPTY);
                        state = State.IN_SEGMENT;
                        break;
                    default:
                        segTokenCount++;
                        currentToken.incrementIndex();
                        currentToken.resetSubElementIndex();
                        currentToken.setType(Token.TokenType.EMPTY);
                }
                currentToken.setLast(true);
                currentToken.resetValue();
                break;

            case SUB_DELIMITER:
                switch (state) {
                    case IN_SEGMENT:
                        if (!repetition)
                            currentToken.incrementIndex();
                        state = State.IN_COMPOSITE;
                        currentToken.resetSubElementIndex();
                        break;
                    case IN_COMPOSITE:
                        currentToken.incrementSubElementIndex();
                }
                currentToken.setLast(false);
                currentToken.setType(Token.TokenType.SUB_EMPTY);
                currentToken.resetValue();
                break;

            case REPEAT_DELIMITER:
                switch (state) {
                    case IN_COMPOSITE:
                        // return an empty subelement token, marked as last
                        currentToken.incrementSubElementIndex();
                        currentToken.setLast(true);
                        currentToken.setType(Token.TokenType.SUB_EMPTY);
                        currentToken.resetValue();
                        state = State.IN_SEGMENT;
                        repetition = true;
                        break;
                    case IN_SEGMENT:
                        // return an empty element
                        currentToken.incrementIndex();
                        currentToken.resetSubElementIndex();
                        currentToken.setLast(false);
                        currentToken.setType(Token.TokenType.EMPTY);
                        currentToken.resetValue();
                        repetition = true;
                }
                break;

            case EOF:
                currentToken.setType(Token.TokenType.END_OF_DATA);
                tokenReady = false;
                break;
        }
    }

}
