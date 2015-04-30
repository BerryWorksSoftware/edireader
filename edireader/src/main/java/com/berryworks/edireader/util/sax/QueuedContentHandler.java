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
import com.berryworks.edireader.tokenizer.SourcePosition;
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
    private final LinkedList<QueuedItem> queue = new LinkedList<>();
    private final int queueSizeLimit;
    private final SourcePosition sourcePosition;

    public QueuedContentHandler(ContentHandler handler, int queueSizeLimit, SourcePosition sourcePosition) {
        wrappedHandler = handler;
        this.queueSizeLimit = queueSizeLimit;
        this.sourcePosition = sourcePosition;
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

    public void drainQueue() throws SAXException {
        while (queue.size() > 0) {
            queue.removeFirst().process(wrappedHandler);
        }
        if (wrappedHandler instanceof SourcePosition) {
            ((SourcePosition) wrappedHandler).setCharCounts(-1, -1);
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        limitSize();
        final int charCount = sourcePosition == null ? 0 : sourcePosition.getCharCount();
        final int segmentCharCount = sourcePosition == null ? 0 : sourcePosition.getSegmentCharCount();
        queue.add(new QueuedStartItem(uri, localName, qName, attributes, charCount, segmentCharCount));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        limitSize();
        final int charCount = sourcePosition == null ? 0 : sourcePosition.getCharCount();
        final int segmentCharCount = sourcePosition == null ? 0 : sourcePosition.getSegmentCharCount();
        queue.add(new QueuedEndItem(uri, localName, qName, charCount, segmentCharCount));
    }

    private void limitSize() throws SAXException {
        while (queue.size() >= queueSizeLimit) {
            final QueuedItem queuedItem = queue.removeFirst();
            queuedItem.process(wrappedHandler);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        characters(new String(chars, start, length));
    }

    public void characters(String data) {
        queue.getLast().addData(data);
    }

    public String getAttribute(String tag, String attributeName) {
        for (int i = queue.size() - 1; i >= 0; i--) {
            QueuedItem queuedItem = queue.get(i);
            if (tag.equals(queuedItem.getLocalName())) {
                EDIAttributes attributes = queuedItem.getAttributes();
                return attributes.getValue(attributeName);
            }
        }
        return null;
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

    public ContentHandler getWrappedContentHandler() {
        return wrappedHandler;
    }

    abstract class QueuedItem {
        private final String uri;
        private final String localName;
        private final String qName;
        private final int charCount;
        private final int segmentCharCount;

        QueuedItem(String uri, String localName, String qName, int charCount, int segmentCharCount) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            this.charCount = charCount;
            this.segmentCharCount = segmentCharCount;
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

        public int getCharCount() {
            return charCount;
        }

        public int getSegmentCharCount() {
            return segmentCharCount;
        }
    }

    class QueuedStartItem extends QueuedItem {
        private final EDIAttributes attributes;
        private String data;

        QueuedStartItem(String uri, String localName, String qName, Attributes attributes, int charCount, int segmentCharCount) {
            super(uri, localName, qName, charCount, segmentCharCount);
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
            if (handler instanceof SourcePosition) {
                ((SourcePosition) handler).setCharCounts(getCharCount(), getSegmentCharCount());
            }
            handler.startElement(uri, name, qname, attributes1);
            if (getData() != null) {
                char[] ca = getData().toCharArray();
                handler.characters(ca, 0, ca.length);
            }
        }
    }

    class QueuedEndItem extends QueuedItem {

        QueuedEndItem(String uri, String localName, String qName, int charCount, int segmentCharCount) {
            super(uri, localName, qName, charCount, segmentCharCount);
        }

        @Override
        public void process(ContentHandler handler) throws SAXException {
            if (handler instanceof SourcePosition) {
                ((SourcePosition) handler).setCharCounts(getCharCount(), getSegmentCharCount());
            }
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
