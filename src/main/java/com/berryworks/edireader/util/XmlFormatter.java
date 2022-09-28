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

package com.berryworks.edireader.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class XmlFormatter extends FilterWriter {
    private final static String SEPARATOR = System.getProperty("line.separator");

    private final static String INDENT = "    ";
    private char mostRecentCharOfInterest;
    private String currentIndent = "";

    private enum State {
        NORMAL_TEXT,
        HOLDING_A_CLOSE,
        HOLDING_A_CLOSE_FOLLOWED_BY_OPEN
    }

    private State state;

    public XmlFormatter(Writer writer) {
        super(writer);
        state = State.NORMAL_TEXT;
    }

    @Override
    public void write(int i) throws IOException {
        if (i <= 0) {
            super.write(i);
        } else {
            process((char) i);
        }
    }

    protected void process(char c) throws IOException {

        switch (state) {
            case NORMAL_TEXT:
                if (c == '>') {
                    state = State.HOLDING_A_CLOSE;
                    break;
                } else {
                    if (isCharacterOfInterest(c)) {
                        mostRecentCharOfInterest = c;
                    }
                    out.write(c);
                }
                break;

            case HOLDING_A_CLOSE:
                if (c == '<') {
                    state = State.HOLDING_A_CLOSE_FOLLOWED_BY_OPEN;
                    break;
                } else {
                    out.write('>');
                    out.write(c);
                    state = State.NORMAL_TEXT;
                }
                break;

            case HOLDING_A_CLOSE_FOLLOWED_BY_OPEN:
                out.write('>');
                out.write(indentionAsNeeded(c));
                out.write('<');
                out.write(c);
                state = State.NORMAL_TEXT;
                break;
        }

    }

    private String indentionAsNeeded(char currentChar) {
        if (currentChar == '/') {
            // ...<def><abc></def...
            currentIndent = shrink(currentIndent);
        } else if (mostRecentCharOfInterest == '?') {
            // <?xml...?><root...
            mostRecentCharOfInterest = '<';
        } else {
            // ...abc><def...
            if (mostRecentCharOfInterest == '/') {
                // do not increase the indent
                mostRecentCharOfInterest = '<';
            } else {
                currentIndent += INDENT; // expand
            }
        }
        return SEPARATOR + currentIndent;
    }

    private boolean isCharacterOfInterest(char c) {
        return c == '<' || c == '?' || c == '/';
    }

    @Override
    public void write(char[] chars, int startIndex, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            process(chars[startIndex + i]);
        }
    }

    @Override
    public void write(String text, int startIndex, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            process(text.charAt(startIndex + i));
        }
    }

    @Override
    public void close() throws IOException {
        switch (state) {
            case NORMAL_TEXT:
                break;

            case HOLDING_A_CLOSE:
                out.write('>');
                break;

            case HOLDING_A_CLOSE_FOLLOWED_BY_OPEN:
                out.write('>');
                out.write('<');
                break;
        }

        super.close();
    }

    public static String format(String xmlText) {

        String indent = "";

        int startLookingAtIndex = 0;

        while (true) {

            int i = xmlText.indexOf("><", startLookingAtIndex);
            if (i < 0) {
                break;
            }

            char peekAhead = xmlText.charAt(i + 2);

            int delta = changeIndent(xmlText, i, peekAhead);
            if (delta < 0) {
                indent = shrink(indent);
            } else if (delta > 0) {
                indent += INDENT;
            }

            String insertedWhitespace = SEPARATOR + indent;
            xmlText = xmlText.substring(0, i + 1) + insertedWhitespace + xmlText.substring(i + 1);

            startLookingAtIndex = i + insertedWhitespace.length() + 1;

        }

        return xmlText;
    }

    private static String shrink(String indent) {
        if (indent == null || indent.length() <= INDENT.length())
            return "";

        return indent.substring(INDENT.length());
    }

    private static int changeIndent(String xmlText, int startingIndex, char peekAhead) {

        int index = startingIndex;

        while (--index > 0) {
            char c = xmlText.charAt(index);

            switch (c) {
                case '<':
                    return 1;
                case '?':
                    return 0;
                case '/':
                    if (index + 1 == startingIndex) {
                        return -1;
                    } else {
                        return peekAhead == '/' ? -1 : 0;
                    }
            }

        }

        return 0;
    }

    private char priorNonWhitespace(String xmlText, int index) {

        while (true) {

            if (--index <= 0)
                return xmlText.charAt(0);

            char c = xmlText.charAt(index);
            if (!Character.isWhitespace(c))
                return c;
        }
    }

    private boolean isClosing(String xmlText, int index) {

        while (--index > 0) {
            char c = xmlText.charAt(index);

            switch (c) {
                case '<':
                    return false;
                case '?':
                case '/':
                    return true;
            }

        }

        return false;
    }
}
