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

package com.berryworks.edireader.util.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * This implementation of a SAX ContentHandler
 * turns the sequence of SAX method calls received from
 * a parser into a stream of serialized Java objects.
 */
public class SAXObjectHandler extends DefaultHandler {

    private ObjectOutputStream objectStream;
    private int sAXEventsWritten;
    private boolean disabled;

    public SAXObjectHandler(OutputStream outputStream) throws IOException {
        objectStream = new ObjectOutputStream(outputStream);
    }

    @Override
    public void startDocument() {
        if (disabled)
            return;

        try {
            objectStream.writeObject(new SAXStartDocument());
            sAXEventsWritten++;
        } catch (IOException e) {
            e.printStackTrace();
            disable();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        if (disabled)
            return;

        try {
            objectStream.writeObject(new SAXEndDocument());
            sAXEventsWritten++;
        } catch (IOException e) {
            e.printStackTrace();
            disable();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (disabled)
            return;

        try {
            objectStream.writeObject(new SAXStartElement(uri, localName, qName, attributes));
            sAXEventsWritten++;
        } catch (IOException e) {
            e.printStackTrace();
            disable();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (disabled)
            return;

        try {
            objectStream.writeObject(new SAXEndElement(uri, localName, qName));
            sAXEventsWritten++;
        } catch (IOException e) {
            e.printStackTrace();
            disable();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (disabled)
            return;

        try {
            objectStream.writeObject(new SAXCharacters(ch, start, length));
            sAXEventsWritten++;
        } catch (IOException e) {
            e.printStackTrace();
            disable();
        }
    }

    public int getSAXEventsWritten() {
        return sAXEventsWritten;
    }

    public void markEndOfStream() throws IOException {
        if (disabled)
            return;

        objectStream.writeObject(new SAXEndOfStreamMarker());
    }

    public void disable() {
        disabled = true;
        try {
            objectStream.close();
        } catch (IOException ignore) {
        }
    }

}
