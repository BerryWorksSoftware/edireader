package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class QueuedContentHandlerTest {
    private QueuedContentHandler handler;
    private SimpleHandler wrappedHandler;
    private EDIAttributes attributes;

    @Test
    public void canConstruct() {
        wrappedHandler = new SimpleHandler();
        handler = new QueuedContentHandler(wrappedHandler, 10, null);
        assertSame(wrappedHandler, handler.getWrappedContentHandler());
    }

    @Test
    public void passesThroughAllItems() throws SAXException {
        wrappedHandler = new SimpleHandler();
        handler = new QueuedContentHandler(wrappedHandler, 10, null);
        attributes = new EDIAttributes();
        attributes.addCDATA("a", "a");
        handler.startElement("", "A", "A", attributes);
        handler.characters("data");
        handler.characters("+more".toCharArray(), 0, 5);
        handler.endElement("", "A", "A");
        handler.drainQueue();
        assertEquals(1, wrappedHandler.getElementCount());
        assertEquals("(A.a = a:data+more)", wrappedHandler.getImage());
    }

    @Test
    public void canGetFirstAttributes() throws SAXException {
        wrappedHandler = new SimpleHandler();
        handler = new QueuedContentHandler(wrappedHandler, 10, null);

        attributes = new EDIAttributes();
        attributes.addCDATA("a", "a");
        handler.startElement("", "A", "A", attributes);
        handler.characters("data");
        handler.endElement("", "A", "A");

        handler.startElement("", "B", "B", null);
        handler.characters("more");
        handler.endElement("", "B", "B");

        assertEquals("a = a", handler.getFirstAttributes().toString());
    }

    @Test
    public void enforcesQueueSizeLimit() throws SAXException {
        wrappedHandler = new SimpleHandler();
        handler = new QueuedContentHandler(wrappedHandler, 2, null);

        attributes = new EDIAttributes();
        attributes.addCDATA("a", "a");
        handler.startElement("", "A", "A", null);
        handler.characters("data");
        handler.endElement("", "A", "A");

        handler.startElement("", "B", "B", null);
        handler.endElement("", "B", "B");
        handler.startElement("", "C", "C", null);
        handler.endElement("", "C", "C");

        // Note that we have not drained the queue.
        assertEquals(2, wrappedHandler.getElementCount());
        assertEquals("(A.empty:data)(B.empty)", wrappedHandler.getImage());
    }

    private static class SimpleHandler extends DefaultHandler {
        private int elementCount;
        private final StringBuilder image = new StringBuilder();
        private boolean hasData;

        public int getElementCount() {
            return elementCount;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            elementCount++;
            image.append("(" + localName);
            hasData = false;
            if (attributes != null) {
                image.append("." + attributes);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            image.append(")");
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (!hasData) {
                image.append(":");
                hasData = false;
            }
            image.append(String.valueOf(ch, start, length));
        }

        public String getImage() {
            return image.toString();
        }
    }
}
