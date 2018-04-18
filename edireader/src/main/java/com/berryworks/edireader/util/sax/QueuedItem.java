package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class QueuedItem {
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
