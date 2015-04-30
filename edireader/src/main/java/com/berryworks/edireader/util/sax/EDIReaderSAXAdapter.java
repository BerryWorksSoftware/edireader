/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.XMLTags;
import com.berryworks.edireader.tokenizer.SourcePosition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.PrintStream;

/**
 * A SAX ContentHandler with specific knowledge of the XML structures emitted by EDIReader.
 * If adapts the general purpose SAX ContentHandler API to an API that receives calls in terms
 * of EDI interchanges, groups, transactions, etc.
 * <p/>
 * If you need to develop a SAX back-end to process EDI content parsed by EDIReader,
 * it may be convenient to inherit from this class and avoid some of the tedious aspects
 * of detecting EDI structures in the SAX interface.
 * <p/>
 * See also com.berryworks.edireader.SAXAdapter which serves a similar purpose.
 * These two classes need to combined into a single class.
 */
public class EDIReaderSAXAdapter extends DefaultHandler implements SourcePosition {
    protected final XMLTags xmlTags;

    protected boolean anotherSEG, implicitGroup, implicitDocument;
    private int charCount = -1;
    private int segmentCharCount = -1;

    public boolean isImplicitInterchangeTermination() {
        return implicitInterchangeTermination;
    }

    private boolean implicitInterchangeTermination;

    protected String elementString, subElementString;

    public EDIReaderSAXAdapter(XMLTags xmlTags) {
        this.xmlTags = xmlTags;
    }

    @Override
    public void startElement(String namespace, String localName, String qName,
                             Attributes atts) throws SAXException {
        int charCount = getCharCount();
        int segmentCharCount = getSegmentCharCount();

        if (localName.startsWith(xmlTags.getInterchangeTag())) {
            anotherSEG = false;
            beginInterchange(charCount, segmentCharCount);
        } else if (localName.startsWith(xmlTags.getGroupTag())) {
            anotherSEG = false;
            if (atts.getLength() == 0) {
                implicitGroup = true;
                beginImplicitGroup();
            } else
                beginExplicitGroup(charCount, segmentCharCount);
        } else if (localName.startsWith(xmlTags.getDocumentTag())) {
            anotherSEG = false;
            if (atts.getLength() == 0) {
                implicitDocument = true;
                beginImplicitDocument();
            } else {
                beginDocument(charCount, segmentCharCount, atts);
            }
        } else if (localName.startsWith(xmlTags.getSegTag())) {
            if (anotherSEG)
                beginAnotherSegment(atts);
            else {
                beginFirstSegment(atts);
                anotherSEG = true;
            }
        } else if (localName.startsWith(xmlTags.getLoopTag())) {
            anotherSEG = true;
            String loopName = "";
            if (atts.getLength() > 0)
                loopName = atts.getValue(0);
            beginSegmentGroup(loopName, atts);
        } else if (localName.startsWith(xmlTags.getElementTag())) {
            elementString = "";
            beginSegmentElement(atts);
        } else if (localName.startsWith(xmlTags.getSubElementTag())) {
            subElementString = "";
            beginSegmentSubElement(atts);
        }
    }

    @Override
    public void setCharCounts(int charCount, int segmentCharCount) {
        this.charCount = charCount;
        this.segmentCharCount = segmentCharCount;
    }


    @Override
    public int getCharCount() {
        return charCount;
    }

    @Override
    public int getSegmentCharCount() {
        return segmentCharCount;
    }

    @Override
    public void endElement(String namespace, String localName, String qName)
            throws SAXException {
        int charCount = getCharCount();
        int segmentCharCount = getSegmentCharCount();

        if (localName.startsWith(xmlTags.getInterchangeTag())) {
            anotherSEG = false;
            endInterchange(charCount, segmentCharCount);
            implicitInterchangeTermination = false;
        } else if (localName.startsWith(xmlTags.getGroupTag())) {
            anotherSEG = false;
            if (implicitGroup)
                endImplicitGroup();
            else
                endExplicitGroup(charCount, segmentCharCount);
        } else if (localName.startsWith(xmlTags.getDocumentTag())) {
            anotherSEG = false;
            if (!implicitDocument)
                endDocument(charCount, segmentCharCount);
        } else if (localName.startsWith(xmlTags.getSegTag())) {
            endSegment(charCount, segmentCharCount);
        } else if (localName.startsWith(xmlTags.getElementTag())) {
            endSegmentElement(elementString);
            elementString = null;
        } else if (localName.startsWith(xmlTags.getSubElementTag())) {
            endSegmentSubElement(subElementString);
            subElementString = null;
        } else if (localName.startsWith(xmlTags.getLoopTag())) {
            endSegmentGroup();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String s = new String(ch, start, length);
        if (elementString != null) elementString += s;
    }

    public void preface() {
    }

    public void addendum() {
    }

    protected void beginInterchange(int charCount, int segmentCharCount) {
    }

    protected void endInterchange(int charCount, int segmentCharCount) {
    }

    protected void beginExplicitGroup(int charCount, int segmentCharCount) {
    }

    protected void endExplicitGroup(int charCount, int segmentCharCount) {
    }

    protected void beginImplicitGroup() {
    }

    protected void endImplicitGroup() {
    }

    protected void beginSegmentGroup(String loopName, Attributes atts) {
    }

    protected void beginFirstSegment(Attributes atts) {
        beginAnotherSegment(atts);
    }

    protected void beginAnotherSegment(Attributes atts) {
    }

    protected void beginDocument(int charCount, int segmentCharCount,
                                 Attributes attributes) {
    }

    protected void endDocument(int charCount, int segmentCharCount) {
    }

    protected void beginImplicitDocument() {
    }

    protected void endSegment(int charCount, int segmentCharCount) {
    }

    protected void endSegmentGroup() {
    }

    protected void beginSegmentElement(Attributes atts) {
    }

    protected void endSegmentElement(String elementString) {
    }

    protected void beginSegmentSubElement(Attributes atts) {
    }

    protected void endSegmentSubElement(String subElementString) {
    }

    protected void recover(Exception e) {
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if ("interchangeTermination".equals(target) && "implicit".equals(data)) {
            implicitInterchangeTermination = true;
        }
    }

    protected void printAttributes(Attributes atts, PrintStream out) {
        for (int i = 0; i < atts.getLength(); i++) {
            out.println("attribute " + i + ": " + atts.getLocalName(i) + "=" + atts.getValue(i));
        }
    }

}
