package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.tokenizer.SourcePosition;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class QueuedEndItem extends QueuedItem {

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
