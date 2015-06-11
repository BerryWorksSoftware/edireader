/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CustomXMLTagsTest {

    private ContentHandler handler;

    private InputSource inputSource;

    private XMLTags customTags;

    private String standardRootTag;

    @Before
    public void setUp() {
        handler = new MyHandler();
        standardRootTag = DefaultXMLTags.getInstance().getRootTag();
        customTags = new CustomXMLTags();
        String customRootTag = customTags.getRootTag();
        assertEquals("ediroot", standardRootTag);
        assertEquals("xediroot", customRootTag);
        inputSource = EDITestData.getAnsiInputSource();
    }

    @Test
    public void testWithSpecificParser() throws IOException, SAXException {
        EDIReader parser = new AnsiReader();
        parser.setContentHandler(handler);
        parser.setXMLTags(customTags);
        parser.parse(inputSource);
    }

    @Test
    public void testWithEDIReader() throws IOException, SAXException {
        EDIReader parser = new EDIReader();
        parser.setContentHandler(handler);
        parser.setXMLTags(customTags);
        parser.parse(inputSource);
    }

    @Test
    public void testWithEDIReaderFactory() throws IOException, SAXException {
        EDIReader parser = EDIReaderFactory.createEDIReader(inputSource);
        parser.setContentHandler(handler);
        parser.setXMLTags(customTags);
        parser.parse(inputSource);
    }

    @Test
    public void testWithJAXP() throws Exception {
        System.setProperty("javax.xml.parsers.SAXParserFactory",
                "com.berryworks.edireader.EDIParserFactory");
        SAXParserFactory sFactory = SAXParserFactory.newInstance();
        SAXParser sParser = sFactory.newSAXParser();
        XMLReader xmlReader = sParser.getXMLReader();
        xmlReader.setContentHandler(handler);
        ((EDIReader) xmlReader).setXMLTags(customTags);
        xmlReader.parse(inputSource);
    }

    class CustomXMLTags extends DefaultXMLTags {
        public String getRootTag() {
            return "xediroot";
        }
    }

    private class MyHandler extends DefaultHandler {
        public void startElement(String namespace, String localName,
                                 String qName, Attributes atts) throws SAXException {
            if (qName.equals(standardRootTag)) {
                fail("got standard root tag instead of custom root tag");
            }
        }

    }

}
