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

import com.berryworks.edireader.EDIAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of a SAX ContentHandler
 * maintains a stack corresponding to the nested XML elements.
 * It is not used in EDIReader, but can be convenient in certain
 * situations for processing the SAX output from EDIReader.
 */
public class StackedContentHandler extends DefaultHandler {
    private final ContentHandler wrappedHandler;
    private final List<StackedItem> stack = new ArrayList<StackedItem>();

    public StackedContentHandler(ContentHandler handler) {
        wrappedHandler = handler;
    }

    @Override
    public void startDocument() throws SAXException {
        wrappedHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        wrappedHandler.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        displayStack("startElement " + localName);
        stack.add(new StackedItem(uri, localName, qName, attributes));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        displayStack("endElement " + localName);
        for (StackedItem stackedItem : stack)
            if (!stackedItem.isStarted())
                stackedItem.start(wrappedHandler);

        pop().end(wrappedHandler);
    }

    private StackedItem pop() {
        int n = stack.size();
        if (n == 0)
            throw new RuntimeException("attempt to pop an empty stack");
        return stack.remove(n - 1);
    }

    private void displayStack(String msg) {
        System.out.println(msg + ", stack size " + stack.size());

        for (StackedItem stackedItem : stack) {
            System.out.println("..." + stackedItem.getLocalName());
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        characters(new String(chars, start, length));
    }

    public void characters(String data) {
        stack.get(stack.size() - 1).addData(data);
    }

    public void addAttribute(String tag, String attributeName, String data) {

        for (StackedItem stackedItem : stack) {
            if (tag.equals(stackedItem.getLocalName())) {
                EDIAttributes attributes = stackedItem.getAttributes();
                attributes.addCDATA(attributeName, data);
//                System.out.println("Added " + attributeName + "=" + data + " to " + tag);
                return;
            }
        }
        throw new RuntimeException("Could not find stacked element " + tag + " for putAttribute()");
    }

    static class StackedItem {
        private final String uri;
        private final String localName;
        private final String qName;
        private final EDIAttributes attributes;
        private String data;
        private boolean started;

        StackedItem(String uri, String localName, String qName, Attributes attributes) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            this.attributes = new EDIAttributes(attributes);
        }

        public String getUri() {
            return uri;
        }

        public String getLocalName() {
            return localName;
        }

        public String getQName() {
            return qName;
        }

        public EDIAttributes getAttributes() {
            return attributes;
        }

        public String getData() {
            return data;
        }

        public boolean isStarted() {
            return started;
        }

        public void addData(String data) {
            if (this.data == null)
                this.data = data;
            else
                this.data += data;
        }

        public void start(ContentHandler handler) throws SAXException {
            handler.startElement(uri, localName, qName, attributes);
            if (data != null)
                handler.characters(data.toCharArray(), 0, data.length());
            started = true;
        }

        public void end(ContentHandler handler) throws SAXException {
            handler.endElement(uri, localName, qName);
        }
    }
}