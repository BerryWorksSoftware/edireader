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

/**
 * A token noted by EDITokenizer.
 */
public interface Token {

    enum TokenType {
        UNKNOWN, SEGMENT_START, SIMPLE, EMPTY, SUB_ELEMENT, SUB_SUB_ELEMENT, SUB_EMPTY, SUB_SUB_EMPTY, SEGMENT_END, END_OF_DATA
    }

    int getValueLength();

    void append(char c);

    void resetValue();

    void incrementSubElementIndex();

    void setType(TokenType tokenType);

    void setLast(boolean value);

    void setValue(char c);

    void incrementIndex();

    void resetSubElementIndex();

    void resetIndexes();

    void setSegmentType(String s) throws EDISyntaxException;

    boolean containsNonSpace();

    /**
     * Gets the type of the token.
     *
     * @return SEGMENT_START, SIMPLE, SUB_ELEMENT, ...
     */
    TokenType getType();


    /**
     * Is true for the first sub-element in a series of sub-elements.
     *
     * @return boolean
     */
    boolean isFirst();


    /**
     * Is true for the last sub-element in a series of sub-elements.
     *
     * @return boolean
     */
    boolean isLast();


    /**
     * Gets the index of an element in its segment, origin 0.
     * The segment type is at index 0, and therefore the first element within the segment has index 1.
     *
     * @return int index
     */
    int getIndex();


    /**
     * Gets the index, origin 0, of a sub-element within a series of sub-elements within a composite element.
     *
     * @return int index
     */
    int getSubIndex();


    /**
     * Gets the value of a token.
     * <p>
     * If this token is of type SEGMENT_START, the value of getSegmentType() is returned.
     * For an ELEMENT, SUB_ELEMENT, or SUB_SUB_ELEMENT, the value is the actual EDI value.
     *
     * @return The value
     */
    String getValue();


    /**
     * Gets the same thing as <code>getValue</code>, returning it
     * as a <code>char[]</code>.
     *
     * @return The valueChars value
     */
    char[] getValueChars();


    /**
     * Returns true if the value of this token equals
     * the argument.
     *
     * @param v Description of the Parameter
     * @return Description of the Return Value
     */
    boolean valueEquals(String v);


    /**
     * Gets the value of the first token in the segment.
     *
     * @return The segmentType value
     */
    String getSegmentType();


    /**
     * Returns a String concatenation of the segment type and
     * a two-digit (or more) representation of the token's
     * getIndex() value.
     *
     * @return The elementIdS value
     */
    String getElementId();

}

