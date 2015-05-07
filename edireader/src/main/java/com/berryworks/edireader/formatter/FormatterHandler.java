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

package com.berryworks.edireader.formatter;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * A SAX Handler used within the Formatter utility program to receive the "XML"
 * elements parsed by EDIReader.
 */
public class FormatterHandler extends EDIReaderSAXAdapter {
    private final Reader input;
    protected final PrintWriter output;
    protected final EDIReader ediReader;
    protected String indent;
    private String filename;
    private int charsReadSoFar;

    public FormatterHandler(EDIReader reader, Reader input, PrintWriter output) {
        super(reader.getXMLTags());
        this.ediReader = reader;
        this.input = input;
        this.output = output;
    }

    @Override
    protected void beginInterchange(int charCount, int segmentCharCount) {
        indent = "";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void endInterchange(int charCount, int segmentCharCount) {
        indent = "";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void beginExplicitGroup(int charCount, int segmentCharCount) {
        indent = "   ";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void endExplicitGroup(int charCount, int segmentCharCount) {
        indent = "   ";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void beginImplicitGroup() {
        indent = "   ";
        endImplicitGroup();
    }

    @Override
    protected void endImplicitGroup() {
        output.println(indent + "...");
    }

    @Override
    protected void beginSegmentGroup(String loopName, Attributes atts) {
        output.println(indent + "+------ " + loopName);
        indent = indent + "|  ";
    }

    @Override
    protected void beginAnotherSegment(Attributes atts) {
        super.beginAnotherSegment(atts);
    }

    @Override
    protected void beginFirstSegment(Attributes atts) {
        indent = indent + "   ";
    }

    @Override
    protected void beginDocument(int charCount, int segmentCharCount,
                                 Attributes attributes) {
        indent = "      ";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void endDocument(int charCount, int segmentCharCount) {
        // System.err.println("endDocument");
        indent = "      ";
        String segment = readSegment((charCount - segmentCharCount), segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void beginImplicitDocument() {
        // System.err.println("begin implicit document");
        indent = "     ";
    }

    @Override
    protected void endSegment(int charCount, int segmentCharCount) {
        String segment = readSegment((charCount - segmentCharCount),
                segmentCharCount);
        output.println(indent + segment);
    }

    @Override
    protected void endSegmentGroup() {
        indent = indent.substring(0, indent.length() - 3);
        output.println(indent + "+-----------");
    }

    protected String readSegment(int offset, int length) {

        char[] buf;
        // First deal with skipping ahead if needed
        if (charsReadSoFar < offset) {
            int toSkip = offset - charsReadSoFar;
            while (toSkip > 0) {
                try {
                    if (!input.ready())
                        Thread.yield();
                    long n = input.skip(toSkip);
                    if (n > 0) {
                        toSkip -= n;
                        charsReadSoFar += n;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Unexpected skip exception on duplicate pipe ", e);
                }
            }
        }
        if (charsReadSoFar == offset) {
            // normal case: no skipped chars
            buf = new char[length];
            int n = 0;
            while (n < length) {
                try {
                    if (!input.ready())
                        Thread.yield();
                    int i = input.read(buf, n, length - n);
                    if (i == -1)
                        break;
                    else if (i < length - n)
                        Thread.yield();
                    if (i > 0) {
                        n += i;
                        charsReadSoFar += i;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Unexpected read exception on duplicate pipe ", e);
                }
            }
        } else {
            throw new RuntimeException("Unexpected attempt to skip backwards");
        }

        // Determine the index of the last non-whitespace and non-control
        // char in the segment
        int i;
        for (i = buf.length; i > 0; i--)
            if ((Tokenizer.WHITESPACE.indexOf(buf[i - 1]) < 0)
                    && (!Character.isISOControl(buf[i - 1])))
                break;
        return new String(buf, 0, i);
    }

    @Override
    protected void recover(Exception e) {
        System.err.println("recovering from " + e.getMessage());
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public int getSegmentCharCount() {
        int n = super.getSegmentCharCount();
        return n == -1 ? ediReader.getSegmentCharCount() : n;
    }

    @Override
    public int getCharCount() {
        int n = super.getCharCount();
        return n == -1 ? ediReader.getCharCount() : n;
    }

    public void start(String uri, String name, String data, Attributes attributes) {
    }

    public void end(String uri, String name) {
    }

    @Override
    public void preface() {
    }

    @Override
    public void addendum() {
    }

}