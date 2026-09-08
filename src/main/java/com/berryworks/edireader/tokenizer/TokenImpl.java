/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.tokenizer;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.error.ErrorMessages;

import java.nio.Buffer;
import java.nio.CharBuffer;

public class TokenImpl implements Token {

    private final Tokenizer tokenizer;
    private final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private CharBuffer valueBuffer = CharBuffer.wrap(new char[10]);
    private TokenType type = TokenType.UNKNOWN;
    private int index, subElementIndex, subSubElementIndex;
    private int elementRepetition;
    private boolean lastSubElement;
    private boolean containsNonSpace;
    private String segmentType = "";

    public TokenImpl(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    /**
     * Returns true for the first subelement in a sequence of subelements.
     *
     * @return The first value
     */
    @Override
    public boolean isFirst() {
        return (subElementIndex == 0);
    }

    /**
     * Returns true if this is the last subelement in a subelement sequence.
     *
     * @return The last value
     */
    @Override
    public boolean isLast() {
        return lastSubElement;
    }

    @Override
    public void setLast(boolean value) {
        lastSubElement = value;
    }

    /**
     * Gets the 0-origin sequential position of this token within the
     * segment.
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     * Gets the 0-origin sequential position of a subelement within a series
     * of subelements.
     */
    @Override
    public int getSubIndex() {
        return subElementIndex;
    }

    /**
     * Gets the index, origin 0, of a sub-sub-element within a series within a sub-element.
     */
    @Override
    public int getSubSubIndex() {
        return subSubElementIndex;
    }

    /**
     * Gets the repetition count for the element referenced by getIndex(),
     * If the instance of the element within the segment is the first one, which is the common case,
     * 0 is returned indicating that it is not a repetition. If it is repeated, the first repetition
     * (which is the 2nd instance in the sequence) will get 1, the 2nd repetition will get 2, etc.
     */
    @Override
    public int getElementRepetition() {
        return elementRepetition;
    }

    @Override
    public void setValue(char c) {
        resetValue();
        append(c);
    }


    /**
     * Gets the data value of the token as a char array.
     *
     * @return The valueChars value
     */
    @Override
    public char[] getValueChars() {
        return valueBuffer.array();
    }

    @Override
    public int getValueLength() {
        return valueBuffer.position();

    }

    /**
     * Gets the data value of the token as a String.
     */
    @Override
    public String getValue() {
        ((Buffer) valueBuffer).flip();
        String s = valueBuffer.toString();
        valueBuffer.compact();
        return s;
    }

    @Override
    public boolean valueEquals(String v) {
        return getValue().equals(v);
    }

    @Override
    public String getSegmentType() {
        return segmentType;
    }

    @Override
    public void setSegmentType(String s) throws EDISyntaxException {
        segmentType = s;
        if ("".equals(segmentType))
            throw new EDISyntaxException(
                    ErrorMessages.INVALID_BEGINNING_OF_SEGMENT, tokenizer);
    }

    @Override
    public boolean containsNonSpace() {
        return containsNonSpace;
    }

    @Override
    public String toString() {
        return "Token type=" + getType() + " " +
                getIndex() + '.' + getSubIndex() + '.' + getSubSubIndex() +
                " value=" + getValue() +
                " segment=" + getSegmentType();
    }

    /**
     * Gets the elementId of the Token.
     *
     * @return The elementId value
     */
    @Override
    public String getElementId() {
        String result = getSegmentType();
        int n = getIndex();
        if (n < 10) {
            return result + '0' + digits[n];
        } else
            return result + n;
    }

    @Override
    public void resetIndexes() {
        index = subElementIndex = subSubElementIndex = elementRepetition = 0;
    }

    @Override
    public void resetSubElementIndex() {
        subElementIndex = 0;
    }

    @Override
    public void resetSubSubElementIndex() {
        subSubElementIndex = 0;
    }

    @Override
    public void incrementIndex() {
        index++;
    }

    @Override
    public void incrementRepetition() {
        elementRepetition++;
    }

    @Override
    public void resetRepetition() {
        elementRepetition = 0;
    }


    @Override
    public void incrementSubElementIndex() {
        subElementIndex++;
    }

    @Override
    public void incrementSubSubElementIndex() {
        subSubElementIndex++;
    }

    @Override
    public void setType(TokenType tokenType) {
        type = tokenType;
    }

    @Override
    public void append(char c) {
        if (!valueBuffer.hasRemaining())
            enlarge();
        valueBuffer.put(c);
        if (!containsNonSpace && c != ' ')
            containsNonSpace = true;
    }

    private void enlarge() {
        CharBuffer newBuffer = CharBuffer.wrap(new char[2 * valueBuffer.capacity()]);
        valueBuffer.compact();
        newBuffer.append(valueBuffer);
        valueBuffer = newBuffer;
    }

    @Override
    public void resetValue() {
        ((Buffer) valueBuffer).clear();
        containsNonSpace = false;
    }
}