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

import java.util.LinkedList;

/**
 * This implementation of a SAX ContentHandler passes SAX events it receives
 * to a delegate ContentHandler with the added value of buffering these events
 * in a queue so that items in the queue may be modified as needed
 * before they are sent to the delegate.
 */
public class QueuedContentHandler extends DefaultHandler {
    private final ContentHandler wrappedHandler;
    private final LinkedList<QueuedItem> queue = new LinkedList<QueuedItem>();
    private final int queueSizeLimit;

    public QueuedContentHandler(ContentHandler handler, int queueSizeLimit) {
        wrappedHandler = handler;
        this.queueSizeLimit = queueSizeLimit;
    }

    @Override
    public void startDocument() throws SAXException {
        wrappedHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        drainQueue();
        wrappedHandler.endDocument();
    }

    private void drainQueue() throws SAXException {
        while (queue.size() > 0) {
            queue.removeFirst().process(wrappedHandler);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        limitSize();
        queue.add(new QueuedStartItem(uri, localName, qName, attributes));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        limitSize();
        queue.add(new QueuedEndItem(uri, localName, qName));
    }

    private void limitSize() throws SAXException {
        while (queue.size() >= queueSizeLimit) {
            queue.removeFirst().process(wrappedHandler);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        characters(new String(chars, start, length));
    }

    public void characters(String data) {
        queue.getLast().addData(data);
    }

    public void putAttribute(String tag, String attributeName, String data) {
        for (int i = queue.size() - 1; i >= 0; i--) {
            QueuedItem queuedItem = queue.get(i);
            if (tag.equals(queuedItem.getLocalName())) {
                EDIAttributes attributes = queuedItem.getAttributes();
                int index = attributes.getIndex(attributeName);
                if (index >= 0)
                    attributes.removeAttribute(index);
                attributes.addCDATA(attributeName, data);
                return;
            }
        }
        throw new RuntimeException("Could not find queued element " + tag + " for putAttribute()");
    }

    EDIAttributes getFirstAttributes() {
        if (queue.size() == 0)
            return null;

        return queue.getFirst().getAttributes();
    }

    abstract class QueuedItem {
        private final String uri;
        private final String localName;
        private final String qName;

        QueuedItem(String uri, String localName, String qName) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
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

        public void end(ContentHandler handler) throws SAXException {
            handler.endElement(uri, localName, qName);
        }

        public abstract void process(ContentHandler handler) throws SAXException;

        public abstract void addData(String data);

        public abstract EDIAttributes getAttributes();
    }

    class QueuedStartItem extends QueuedItem {
        private final EDIAttributes attributes;
        private String data;

        QueuedStartItem(String uri, String localName, String qName, Attributes attributes) {
            super(uri, localName, qName);
            this.attributes = new EDIAttributes(attributes);
        }

        @Override
        public EDIAttributes getAttributes() {
            return attributes;
        }

        public String getData() {
            return data;
        }

        @Override
        public void addData(String data) {
            if (this.data == null)
                this.data = data;
            else
                this.data += data;
        }

        @Override
        public void process(ContentHandler handler) throws SAXException {

            EDIAttributes attributes1 = getAttributes();
            if (attributes1 == null)
                throw new RuntimeException("null attributes");
            String name = getLocalName();
            if (name == null)
                throw new RuntimeException("null name");
            String qname = getQName();
            if (qname == null)
                throw new RuntimeException("null qname");
            String uri = getUri();
            if (uri == null)
                throw new RuntimeException("null uri");
            handler.startElement(uri, name, qname, attributes1);
            if (getData() != null) {
                char[] ca = getData().toCharArray();
                handler.characters(ca, 0, ca.length);
            }
        }
    }

    class QueuedEndItem extends QueuedItem {

        QueuedEndItem(String uri, String localName, String qName) {
            super(uri, localName, qName);
        }

        @Override
        public void process(ContentHandler handler) throws SAXException {
            handler.endElement(getUri(), getLocalName(), getQName());
        }

        @Override
        public void addData(String data) {
            throw new RuntimeException("addData() should not be called on an end item");
        }

        @Override
        public EDIAttributes getAttributes() {
            throw new RuntimeException("getAttributes() should not be called on an end item");
        }
    }
}
