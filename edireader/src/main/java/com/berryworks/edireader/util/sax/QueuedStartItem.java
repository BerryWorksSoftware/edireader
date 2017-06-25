package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.tokenizer.SourcePosition;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class QueuedStartItem extends QueuedItem {
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
