package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface StartWithDataContentHandler extends ContentHandler {
    void startElement(String uri, String name, String qname, EDIAttributes attributes1, String data) throws SAXException;
}
